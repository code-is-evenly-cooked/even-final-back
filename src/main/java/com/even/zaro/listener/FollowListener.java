package com.even.zaro.listener;

import com.even.zaro.config.SpringContext;
import com.even.zaro.entity.Follow;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.repository.NotificationRepository;
import com.even.zaro.service.NotificationSseService;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FollowListener {

    private final NotificationSseService notificationSseService;

    @PostPersist // Follow 엔티티 DB 저장 직후 자동 실행
    @Transactional
    public void onFollowCreated(Follow follow) {

        // 직접 Spring Bean 주입 받기
        NotificationRepository notificationRepository
                = SpringContext.getBean(NotificationRepository.class);

        User followee = follow.getFollowee(); // 팔로우 당한 사용자 (알림 대상)
        User follower = follow.getFollower(); // 팔로우 한 사용자

        Notification notification = new Notification();
        notification.setUser(followee);
        notification.setActorUserId(follower.getId());
        notification.setType(Notification.Type.FOLLOW);
        notification.setTargetId(follower.getId()); // 팔로우 한 사용자 userId
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);

        // sse 실시간 전송
        notificationSseService.send(followee.getId(), saved);
    }
}
