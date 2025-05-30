package com.even.zaro.listener;

import com.even.zaro.config.SpringContext;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.entity.PostLike;
import com.even.zaro.repository.NotificationRepository;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostLikeListener {

    @PostPersist
    @Transactional
    public void onPostLikePersist(PostLike postlike) {

        // 직접 Spring Bean 주입 받기
        NotificationRepository notificationRepository
                = SpringContext.getBean(NotificationRepository.class);

        User postOwner = postlike.getPost().getUser(); // 게시물 작성자
        User likeUser = postlike.getUser(); // 좋아요 누른 유저

        // 댓글 작성자==좋아요 누른 유저 일 때는 알림 생성 X
        if (!postOwner.getId().equals(likeUser.getId())) {
            Notification notification = new Notification();
            notification.setUser(postOwner);
            notification.setType(Notification.Type.LIKE);
            notification.setTargetId(postlike.getPost().getId());
            notification.setRead(false);

            notificationRepository.save(notification);
        }
    }
}
