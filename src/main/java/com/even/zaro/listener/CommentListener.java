package com.even.zaro.listener;

import com.even.zaro.config.SpringContext;
import com.even.zaro.entity.Comment;
import com.even.zaro.service.NotificationService;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentListener {

    @PostPersist // Comment 엔티티 DB 저장 직후 자동 실행
    @Transactional
    public void onCommentCreated(Comment comment) {

        NotificationService notificationService = SpringContext.getBean(NotificationService.class);
        notificationService.createCommentNotification(comment);
    }
}
