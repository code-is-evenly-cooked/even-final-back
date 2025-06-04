package com.even.zaro.listener;

import com.even.zaro.config.SpringContext;
import com.even.zaro.entity.Comment;
import com.even.zaro.event.CommentCreatedEvent;
import com.even.zaro.service.NotificationService;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentListener {

    private final ApplicationEventPublisher eventPublisher;

    @PostPersist // Comment 엔티티 DB 저장 직후 자동 실행
    public void onCommentCreated(Comment comment) {
        eventPublisher.publishEvent(new CommentCreatedEvent(comment));
    }
}
