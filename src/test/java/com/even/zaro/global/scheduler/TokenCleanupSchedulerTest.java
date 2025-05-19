package com.even.zaro.global.scheduler;

import com.even.zaro.entity.*;
import com.even.zaro.repository.EmailTokenRepository;
import com.even.zaro.repository.PasswordResetRepository;
import com.even.zaro.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TokenCleanupSchedulerTest {

    @Autowired
    TokenCleanupScheduler tokenCleanupScheduler;

    @Autowired
    EmailTokenRepository emailTokenRepository;

    @Autowired
    PasswordResetRepository passwordResetRepository;

    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void setupUser() {
        user = userRepository.save(User.builder()
                .email("test@example.com")
                .password("Password1234!")
                .nickname("테스트유저")
                .provider(Provider.LOCAL)
                .status(Status.PENDING)
                .build());
    }

    @Test
    void deleteExpiredEmailToken_givenExpiredToken_shouldDeleteIt() {
        //given
        LocalDateTime expiredAt = LocalDateTime.now().minusDays(2);
        EmailToken token = new EmailToken("token-1", user, expiredAt);
        emailTokenRepository.save(token);

        //when
        tokenCleanupScheduler.deleteExpiredEmailTokens();

        //then
        assertThat(emailTokenRepository.findByToken("token-1")).isEmpty();
    }

    @Test
    void deleteExpiredPasswordTokens_givenExpiredToken_shouldDeleteIt() {
        //given
        LocalDateTime expiredAt = LocalDateTime.now().minusDays(2);
        PasswordResetToken token = new PasswordResetToken("test@example.com", "token-2", expiredAt, false);
        passwordResetRepository.save(token);

        //when
        tokenCleanupScheduler.deleteExpiredPasswordTokens();

        //then
        assertThat(passwordResetRepository.findByToken("token-2")).isEmpty();
    }
}