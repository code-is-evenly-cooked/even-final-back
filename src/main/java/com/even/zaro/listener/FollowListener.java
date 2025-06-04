package com.even.zaro.listener;

import com.even.zaro.config.SpringContext;
import com.even.zaro.entity.Follow;
import com.even.zaro.service.NotificationService;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FollowListener {

    @PostPersist // Follow 엔티티 DB 저장 직후 자동 실행
    @Transactional
    public void onFollowCreated(Follow follow) {

        NotificationService notificationService = SpringContext.getBean(NotificationService.class);
        notificationService.createFollowNotification(follow);
    }
}
