package com.even.zaro.integration;

import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.scheduler.UserScheduler;
import com.even.zaro.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserSchedulerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserScheduler userScheduler;

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
