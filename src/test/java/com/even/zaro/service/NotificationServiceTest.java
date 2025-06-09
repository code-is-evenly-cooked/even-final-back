package com.even.zaro.service;

import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.notification.NotificationException;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
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
    }

    @Nested
    class GetNotificationsListTest {

        @BeforeEach
        void setUp() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        }

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

        @Test
        void 알림_1개_읽음_처리_성공() {
            Notification noti = createNotification(user, Notification.Type.LIKE, false);
            when(notificationRepository.findByIdAndUserId(noti.getId(), userId)).thenReturn(Optional.of(noti));

            notificationService.markAsRead(noti.getId(), userId);

            assertThat(noti.isRead()).isTrue();
        }

        @Test
        void 존재하지_않는_알림_읽음_처리_실패() {
            when(notificationRepository.findByIdAndUserId(9999L, userId)).thenReturn(Optional.empty());

            NotificationException exception = assertThrows(NotificationException.class, () ->
                    notificationService.markAsRead(9999L, userId));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOTIFICATION_NOT_FOUND);
        }

        @Test
        void 이미_읽은_알림을_다시_읽으면_예외_없이_읽음_유지() {
            Notification alreadyRead = createNotification(user, Notification.Type.FOLLOW, true);
            when(notificationRepository.findByIdAndUserId(alreadyRead.getId(), userId)).thenReturn(Optional.of(alreadyRead));

            assertThatCode(() -> notificationService.markAsRead(alreadyRead.getId(), userId))
                    .doesNotThrowAnyException();

            assertThat(alreadyRead.isRead()).isTrue();
        }
    }

    @Nested
    class MarkAllAsReadTest {

        @Test
        void 알림_전체_읽음_처리_성공() {
            Notification n1 = createNotification(user, Notification.Type.FOLLOW, false);
            Notification n2 = createNotification(user, Notification.Type.LIKE, false);

            when(notificationRepository.findAllByUserIdAndIsReadFalse(userId)).thenReturn(List.of(n1, n2));

            notificationService.markAllAsRead(userId);

            assertThat(n1.isRead()).isTrue();
            assertThat(n2.isRead()).isTrue();
        }

        @Test
        void 모든_알림이_이미_읽음상태면_예외없이_전체_유지() {
            when(notificationRepository.findAllByUserIdAndIsReadFalse(userId)).thenReturn(List.of());

            assertThatCode(() -> notificationService.markAllAsRead(userId))
                    .doesNotThrowAnyException();

            verify(notificationRepository).findAllByUserIdAndIsReadFalse(userId);
        }

        @Test
        void 일부_알림만_읽지_않은_경우_읽지_않은_알림만_읽음처리_성공() {
            Notification unread = createNotification(user, Notification.Type.FOLLOW, false);
            Notification alreadyRead = createNotification(user, Notification.Type.LIKE, true);

            // markAllAsRead는 isRead=false인 알림만 가져옴
            when(notificationRepository.findAllByUserIdAndIsReadFalse(userId)).thenReturn(List.of(unread));

            notificationService.markAllAsRead(userId);

            assertThat(unread.isRead()).isTrue();
            assertThat(alreadyRead.isRead()).isTrue();

            verify(notificationRepository).findAllByUserIdAndIsReadFalse(userId);
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

    private NotificationDto createDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .build();
    }
}
