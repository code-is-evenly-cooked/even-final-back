package com.even.zaro.global.scheduler;

import com.even.zaro.repository.EmailTokenRepository;
import com.even.zaro.repository.PasswordResetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final EmailTokenRepository emailTokenRepository;
    private final PasswordResetRepository passwordResetRepository;

    @Scheduled(cron = "0 30 2 * * *")
    @Transactional
    public void deleteExpiredEmailTokens() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        emailTokenRepository.deleteByExpiredAtBefore(oneDayAgo);
    }

    @Scheduled(cron = "0 45 2 * * *")
    @Transactional
    public void deleteExpiredPasswordTokens() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        passwordResetRepository.deleteByExpiredAtBefore(oneDayAgo);
    }
}
