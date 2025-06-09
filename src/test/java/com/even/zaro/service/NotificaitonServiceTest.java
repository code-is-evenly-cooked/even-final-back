package com.even.zaro.service;

import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.global.util.NotificationMapper;
import com.even.zaro.repository.NotificationRepository;
import com.even.zaro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificaitonServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private NotificationSseService notificationSseService;

    private final Long userId = 1L;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(userId).build();
    }

    @Nested
    class GetNotificationsListTest {

        @Test
        void 알림_목록_조회_성공() {
            // given
            Notification noti1 = createNotification(user, Notification.Type.FOLLOW, false);
            Notification noti2 = createNotification(user, Notification.Type.LIKE, false);

            NotificationDto dto1 = NotificationDto.builder()
                    .id(noti1.getId()).type(Notification.Type.FOLLOW).build();
            NotificationDto dto2 = NotificationDto.builder()
                    .id(noti2.getId()).type(Notification.Type.LIKE).build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(notificationRepository.findAllByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(noti1, noti2));
            when(notificationMapper.toDto(noti1)).thenReturn(dto1);
            when(notificationMapper.toDto(noti2)).thenReturn(dto2);

            // when
            List<NotificationDto> result = notificationService.getNotificationsList(userId);

            // then
            assertThat(result).containsExactly(dto1, dto2);
        }

        @Test
        void 알림이_없을_경우_빈리스트_반환() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(notificationRepository.findAllByUserOrderByCreatedAtDesc(user)).thenReturn(List.of());

            List<NotificationDto> result = notificationService.getNotificationsList(userId);

            assertThat(result).isEmpty();
        }
    }

    private Notification createNotification(User user, Notification.Type type, boolean isRead) {
        return Notification.builder()
                .user(user)
                .type(type)
                .targetId(1L)
                .actorUserId(99L)
                .isRead(isRead)
                .build();
    }
}
