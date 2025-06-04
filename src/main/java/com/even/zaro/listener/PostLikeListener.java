package com.even.zaro.listener;

import com.even.zaro.config.SpringContext;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.entity.PostLike;
import com.even.zaro.repository.NotificationRepository;
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
    public void onPostLikePersist(PostLike postlike) {

        // 직접 Spring Bean 주입 받기
        NotificationRepository notificationRepository
                = SpringContext.getBean(NotificationRepository.class);

        NotificationSseService notificationSseService
                = SpringContext.getBean(NotificationSseService.class);

        User postOwner = postlike.getPost().getUser(); // 게시물 작성자
        User likeUser = postlike.getUser(); // 좋아요 누른 유저

        // 댓글 작성자==좋아요 누른 유저 일 때는 알림 생성 X
        if (!postOwner.getId().equals(likeUser.getId())) {
            Notification notification = new Notification();
            notification.setUser(postOwner);
            notification.setActorUserId(likeUser.getId());
            notification.setType(Notification.Type.LIKE);
            notification.setTargetId(postlike.getPost().getId());
            notification.setRead(false);

            Notification saved = notificationRepository.save(notification);

            // sse 실시간 전송
            notificationSseService.send(postOwner.getId(), saved);
        }
    }
}
