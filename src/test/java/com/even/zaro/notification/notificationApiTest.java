package com.even.zaro.notification;

import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.notification.NotificationException;
import com.even.zaro.repository.*;
import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class notificationApiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Test
    void 알림_목록_조회_성공() {
        // given
        User user = createUser("user1@even.com", "유저1닉");
        Notification noti1 = createNotification(user, false);
        Notification noti2 = createNotification(user, true);

        // when
        List<NotificationDto> list = notificationService.getNotificationsList(user.getId());

        // then
        assertThat(list).hasSize(2);
        // 최신순 정렬이어야함 !!
        assertThat(list).extracting("isRead").containsExactly(true, false);
    }

    @Test
    void 알림_1개_읽음_처리_성공() {
        // given
        User user = createUser("user2@even.com", "유저2닉");
        Notification noti = createNotification(user, false);

        // when
        notificationService.markAsRead(noti.getId(), user.getId());

        // then
        Notification found = notificationRepository.findById(noti.getId()).orElseThrow();
        assertThat(found.isRead()).isTrue();
    }

    @Test
    void 알림_전체_읽음_처리_성공() {
        // given
        User user = createUser("user3@even.com", "유저3닉");
        createNotification(user, false);
        createNotification(user, false);

        // when
        notificationService.markAllAsRead(user.getId());

        // then
        List<Notification> list = notificationRepository.findAllByUserIdAndIsReadFalse(user.getId());
        assertThat(list).isEmpty(); // 모두 읽음 처리됨
    }

    @Test
    void 존재하지_않는_알림_읽음_처리_실패() {
        // given
        User user = createUser("user4@even.com", "유저4닉");

        // when & then
        NotificationException exception = assertThrows(NotificationException.class, () ->
                notificationService.markAsRead(9999L, user.getId()));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOTIFICATION_NOT_FOUND);
    }


    private User createUser(String email, String nickname) {
        return userRepository.save(User.builder()
                .email(email)
                .password("password")
                .nickname(nickname)
                .profileImage(null)
                .provider(Provider.LOCAL)
                .status(Status.ACTIVE)
                .build());
    }

    private Notification createNotification(User user, boolean isRead) {
        Notification noti = Notification.builder()
                .user(user)
                .targetId(1L)
                .type(Notification.Type.FOLLOW)
                .isRead(isRead)
                .build();
        return notificationRepository.save(noti);
    }
}
