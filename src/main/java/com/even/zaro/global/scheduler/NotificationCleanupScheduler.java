package com.even.zaro.global.scheduler;

import com.even.zaro.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCleanupScheduler {

    private final NotificationRepository notificationRepository;

    // 매일 새벽 3시에, 그때로부터 30일전 새벽 3시 이전으로 만들어진 알림을 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOldNotifications() {
        LocalDateTime deletingDate = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteByCreatedAtBefore(deletingDate);
        log.info("[Scheduler] 30일 지난 알림 삭제 완료 ! (deletingDate = {})", deletingDate);
    }
}
