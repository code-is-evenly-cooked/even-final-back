package com.even.zaro.global.event.listener;

import com.even.zaro.global.event.event.CommentCreatedEvent;
import com.even.zaro.global.event.event.FollowCreatedEvent;
import com.even.zaro.global.event.event.PostLikeCreatedEvent;
import com.even.zaro.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleComment(CommentCreatedEvent event) {
        notificationService.createCommentNotification(event.getComment());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePostLike(PostLikeCreatedEvent event) {
        notificationService.createPostLikeNotification(event.getPostLike());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleFollow(FollowCreatedEvent event) {
        notificationService.createFollowNotification(event.getFollow());
    }
}
