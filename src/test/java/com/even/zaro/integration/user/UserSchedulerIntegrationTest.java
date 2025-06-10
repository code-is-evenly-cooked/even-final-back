package com.even.zaro.integration.user;

import com.even.zaro.entity.DormancyNoticeLog;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.scheduler.UserScheduler;
import com.even.zaro.repository.DormancyNoticeLogRepository;
import com.even.zaro.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserSchedulerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DormancyNoticeLogRepository dormancyNoticeLogRepository;

    @Autowired
    private UserScheduler userScheduler;

    @PersistenceContext
    private EntityManager em;

    @Nested
    class DormantUserTest {
        @Test
        void shouldSetUserToDormantIfInactiveFor6Months() {
            LocalDateTime now = LocalDateTime.now();

            User activeUser = User.builder()
                    .email("active@even.com")
                    .nickname("액티브")
                    .password("Password1!")
                    .provider(Provider.LOCAL)
                    .status(Status.ACTIVE)
                    .lastLoginAt(now.minusMonths(6).minusDays(1))
                    .build();

            userRepository.save(activeUser);
            em.flush();
            em.clear();

            userScheduler.handleDormantAndSoftDelete();

            User updated = userRepository.findById(activeUser.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(Status.DORMANT);
        }

        @Test
        void shouldSoftDeleteUserIfDormantFor1Year() {
            LocalDateTime now = LocalDateTime.now();

            User dormantUser = User.builder()
                    .email("dormant@even.com")
                    .nickname("도먼트")
                    .password("Password1!")
                    .provider(Provider.LOCAL)
                    .status(Status.DORMANT)
                    .build();

            userRepository.save(dormantUser);

            em.createQuery("UPDATE User u SET u.updatedAt = :updatedAt WHERE u.id = :id")
                    .setParameter("updatedAt", now.minusYears(1).minusDays(1))
                    .setParameter("id", dormantUser.getId())
                    .executeUpdate();

            em.flush();
            em.clear();

            userScheduler.handleDormantAndSoftDelete();

            User updated = userRepository.findById(dormantUser.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(Status.DELETED);
            assertThat(updated.getDeletedAt()).isNotNull();
        }
    }

    @Nested
    class DeleteUserTest {
        @Test
        void shouldDeleteUsersWithdrawnOver30DaysAgo_fromDatabase() {
            User user = User.builder()
                    .email("test@even.com")
                    .nickname("이브니")
                    .password("Password1!")
                    .provider(Provider.LOCAL)
                    .status(Status.DELETED)
                    .deletedAt(LocalDateTime.now().minusDays(31))
                    .build();

            userRepository.save(user);

            userScheduler.deleteWithdrawnUsers();

            assertThat(userRepository.findById(user.getId())).isEmpty();
        }
    }

    @Nested
    class DormantPendingTest {
        @Test
        void shouldSendEmailAndSaveLog_ifNotAlreadySent() {
            LocalDateTime loginAt = LocalDateTime.now().minusMonths(5).minusDays(1);
            User user = userRepository.save(createActiveUser(loginAt));
            em.flush();
            em.clear();

            userScheduler.sendDormancyPendingEmail();

            List<DormancyNoticeLog> logs = dormancyNoticeLogRepository.findAll();
            assertThat(logs).hasSize(1);
            assertThat(logs.getFirst().getUserId()).isEqualTo(user.getId());
        }

        @Test
        void shouldNotSendEmail_ifAlreadySentForSameLoginDate() {
            LocalDateTime loginAt = LocalDateTime.now().minusMonths(5).minusDays(1);
            User user = userRepository.save(createActiveUser(loginAt));
            dormancyNoticeLogRepository.save(
                    new DormancyNoticeLog(user.getId(), loginAt.toLocalDate())
            );
            em.flush();
            em.clear();

            userScheduler.sendDormancyPendingEmail();

            List<DormancyNoticeLog> logs = dormancyNoticeLogRepository.findAll();
            assertThat(logs).hasSize(1);
        }

        @Test
        void shouldSendEmail_ifLastCheckedDateIsDifferent() {
            LocalDateTime baseDate = LocalDateTime.of(2025, 6, 10, 0, 0);
            LocalDateTime loginAt = baseDate.minusMonths(5).minusDays(1);

            User user = userRepository.save(createActiveUser(loginAt));

            dormancyNoticeLogRepository.save(
                    new DormancyNoticeLog(user.getId(), loginAt.minusDays(1).toLocalDate())
            );
            em.flush();
            em.clear();

            userScheduler.sendDormancyPendingEmail();

            List<DormancyNoticeLog> logs = dormancyNoticeLogRepository.findAll();
            assertThat(logs).hasSize(2);
        }

        private User createActiveUser(LocalDateTime lastLoginAt) {
            return User.builder()
                    .email("test@even.com")
                    .nickname("휴면대상")
                    .password("Password1!")
                    .provider(Provider.LOCAL)
                    .status(Status.ACTIVE)
                    .lastLoginAt(lastLoginAt)
                    .build();
        }
    }
}
