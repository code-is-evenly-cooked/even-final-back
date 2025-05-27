package com.even.zaro.global.scheduler;

import com.even.zaro.entity.Notification;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.repository.NotificationRepository;
import com.even.zaro.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class NotificationCleanupSchedulerTest {

    @Autowired
    NotificationCleanupScheduler notificationCleanupScheduler;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void 알림_30일_지나면_스케줄러_삭제_성공() {
        // given
        User user = createUser("test@even.com");
        // 테스트 기준 시간 250526 새벽 3시로 설정
        LocalDateTime testNow = LocalDateTime.of(2025, 5, 26, 3, 0);

        // 기준 시각 : 30일 전 새벽 3시
        LocalDateTime standard = testNow.minusDays(30);

        Notification oldNoti = createNotification(user, false, standard.minusMinutes(1));
        Notification notOldEnoughNoti = createNotification(user, false, standard.plusMinutes(1));

        // when
        notificationCleanupScheduler.deleteOldNotifications(testNow);

        // then
        assertThat(notificationRepository.findById(oldNoti.getId())).isEmpty();
        assertThat(notificationRepository.findById(notOldEnoughNoti.getId())).isPresent();
    }

    private User createUser(String email) {
        return userRepository.save(User.builder()
                .email(email)
                .password("password")
                .nickname("닉네임")
                .provider(Provider.LOCAL)
                .status(Status.ACTIVE)
                .build());
    }

    private Notification createNotification(User user, boolean isRead, LocalDateTime createdAt) {
        Notification noti = Notification.builder()
                .user(user)
                .targetId(1L)
                .type(Notification.Type.FOLLOW)
                .isRead(isRead)
                .createdAt(createdAt)
                .build();
        return notificationRepository.save(noti);
    }
}
