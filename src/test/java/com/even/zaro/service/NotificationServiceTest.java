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
public class NotificationServiceTest {

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
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    }

    @Nested
    class GetNotificationsListTest {

        @Test
        void 알림_목록_조회_성공() {
            // given
            Notification noti1 = createNotification(user, Notification.Type.FOLLOW, false);
            Notification noti2 = createNotification(user, Notification.Type.LIKE, false);

            NotificationDto dto1 = createDto(noti1);
            NotificationDto dto2 = createDto(noti2);

            when(notificationRepository.findAllByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(noti1, noti2));
            when(notificationMapper.toDto(noti1)).thenReturn(dto1);
            when(notificationMapper.toDto(noti2)).thenReturn(dto2);

            // when
            List<NotificationDto> result = notificationService.getNotificationsList(userId);

            // then
            assertThat(result).containsExactly(dto1, dto2);
        }

        @Test
        void 알림이_없을_경우_빈리스트_반환_검증() {
            when(notificationRepository.findAllByUserOrderByCreatedAtDesc(user)).thenReturn(List.of());

            List<NotificationDto> result = notificationService.getNotificationsList(userId);

            assertThat(result).isEmpty();
        }

        @Test
        void 알림_목록의_최신순_정렬_검증() {
            Notification newOne = createNotification(user, Notification.Type.LIKE, false);
            Notification oldOne = createNotification(user, Notification.Type.FOLLOW, false);

            NotificationDto dtoNew = createDto(newOne);
            NotificationDto dtoOld = createDto(oldOne);

            when(notificationRepository.findAllByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(newOne, oldOne));
            when(notificationMapper.toDto(newOne)).thenReturn(dtoNew);
            when(notificationMapper.toDto(oldOne)).thenReturn(dtoOld);

            List<NotificationDto> result = notificationService.getNotificationsList(userId);

            assertThat(result).containsExactly(dtoNew, dtoOld);
        }

    }

    @Nested
    class MarkAsReadTest {

    }

    @Nested
    class MarkAllAsReadTest {

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

    private NotificationDto createDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .build();
    }
}
