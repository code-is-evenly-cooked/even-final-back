package com.even.zaro.notification;

import com.even.zaro.repository.NotificationRepository;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        User user = createUser("test@even.com");
        Notification noti1 = createNotification(user, false);
        Notification noti2 = createNotification(user, true);

        // when
        List<NotificationDto> list = notificationService.getNotificationsList(user.getId());

        // then
        assertThat(list).hasSize(2);
        // 최신순 정렬이어야함 !!
        assertThat(list).extracting("isRead").containsExactly(true, false);
    }

    private User createUser(String email) {
        return userRepository.save(User.builder()
                .email(email)
                .password("password")
                .nickname("닉네임")
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
