package com.even.zaro.listener;

import com.even.zaro.config.SpringContext;
import com.even.zaro.entity.Follow;
import com.even.zaro.event.CommentCreatedEvent;
import com.even.zaro.event.FollowCreatedEvent;
import com.even.zaro.service.NotificationService;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FollowListener {

    private final ApplicationEventPublisher eventPublisher;

    @PostPersist // Follow 엔티티 DB 저장 직후 자동 실행
    @Transactional
    public void onFollowCreated(Follow follow) {
        eventPublisher.publishEvent(new FollowCreatedEvent(follow));
    }
}
