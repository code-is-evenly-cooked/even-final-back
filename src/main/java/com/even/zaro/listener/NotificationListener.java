package com.even.zaro.listener;

import com.even.zaro.event.CommentCreatedEvent;
import com.even.zaro.event.FollowCreatedEvent;
import com.even.zaro.event.PostLikeCreatedEvent;
import com.even.zaro.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleComment(CommentCreatedEvent event) {
        notificationService.createCommentNotification(event.getComment());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostLike(PostLikeCreatedEvent event) {
        notificationService.createPostLikeNotification(event.getPostLike());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFollow(FollowCreatedEvent event) {
        notificationService.createFollowNotification(event.getFollow());
    }
}
