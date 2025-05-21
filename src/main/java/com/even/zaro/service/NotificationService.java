package com.even.zaro.service;

import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.notification.NotificationException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.NotificationRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user).stream()
                .map(notification -> NotificationDto.builder()
                        .id(notification.getId())
                        .type(notification.getType())
                        .targetId(notification.getTargetId())
                        .isRead(notification.isRead())
                        .createdAt(notification.getCreatedAt())
                        .build())
                .toList();
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead(); // isRead = true
    }

    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserIdAndIsReadFalse(userId);
        notifications.forEach(n -> {
            if (!n.isRead()) {
                n.setRead(true);
            }
        });
    }

    // 매일 새벽 3시에, 그때로부터 30일전 새벽 3시 이전으로 만들어진 알림을 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOldNotifications() {
        LocalDateTime deletingDate = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteByCreatedAtBefore(deletingDate);
        log.info("[Scheduler] 30일 지난 알림 삭제 완료 ! (deletingDate = {})", deletingDate);
    }
}
