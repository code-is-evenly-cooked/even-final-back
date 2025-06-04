package com.even.zaro.listener;

import com.even.zaro.config.SpringContext;
import com.even.zaro.entity.Comment;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.repository.NotificationRepository;
import com.even.zaro.service.NotificationService;
import com.even.zaro.service.NotificationSseService;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentListener {

   // private final NotificationSseService notificationSseService;

    @PostPersist // Comment 엔티티 DB 저장 직후 자동 실행
    @Transactional
    public void onCommentCreated(Comment comment) {

        NotificationService notificationService = SpringContext.getBean(NotificationService.class);
        notificationService.createCommentNotification(comment);
    }
}
