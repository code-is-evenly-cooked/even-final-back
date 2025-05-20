package com.even.zaro.listener;

import com.even.zaro.entity.Follow;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.repository.NotificationRepository;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FollowListener {

    @Autowired // 리스너클래스 특성상 Spring에서 자동 Bean생성이 어려워서 필드주입 선택
    private NotificationRepository notificationRepository;

    @PostPersist // Follow 엔티티 DB 저장 직후 자동 실행
    @Transactional
    public void onFollowCreated(Follow follow) {
        User followee = follow.getFollowee(); // 팔로우 당한 사용자 (알림 대상)
        User follower = follow.getFollower(); // 팔로우 한 사용자

        Notification notification = new Notification();
        notification.setUser(followee);
        notification.setType(Notification.Type.FOLLOW);
        notification.setTargetId(follower.getId()); // 팔로우 한 사용자 userId
        notification.setRead(false);

        notificationRepository.save(notification);
    }
}
