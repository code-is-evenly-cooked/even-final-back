package com.even.zaro.dto.notification;

import com.even.zaro.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;

    private Notification.Type type;

    private Long targetId;

    private boolean isRead;

    private LocalDateTime createdAt;
}