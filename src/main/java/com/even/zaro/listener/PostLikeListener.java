package com.even.zaro.listener;

import com.even.zaro.config.SpringContext;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.entity.PostLike;
import com.even.zaro.repository.NotificationRepository;
import com.even.zaro.service.NotificationService;
import com.even.zaro.service.NotificationSseService;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostLikeListener {

//    private final NotificationSseService notificationSseService;

    @PostPersist
    @Transactional
    public void onPostLikePersist(PostLike postLike) {

        NotificationService notificationService = SpringContext.getBean(NotificationService.class);
        notificationService.createPostLikeNotification(postLike);
    }
}
