package com.even.zaro.listener;

import com.even.zaro.config.SpringContext;
import com.even.zaro.entity.PostLike;
import com.even.zaro.event.CommentCreatedEvent;
import com.even.zaro.event.PostLikeCreatedEvent;
import com.even.zaro.service.NotificationService;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostLikeListener {

    private final ApplicationEventPublisher eventPublisher;

    @PostPersist
    public void onPostLikePersist(PostLike postLike) {
        eventPublisher.publishEvent(new PostLikeCreatedEvent(postLike));
    }
}
