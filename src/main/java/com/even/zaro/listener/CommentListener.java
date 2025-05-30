package com.even.zaro.listener;

import com.even.zaro.config.SpringContext;
import com.even.zaro.entity.Comment;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.repository.NotificationRepository;
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

        // 직접 Spring Bean 주입 받기
        NotificationRepository notificationRepository
                = SpringContext.getBean(NotificationRepository.class);

        User postOwner = comment.getPost().getUser(); // 게시글 작성자
        User commentAuthor = comment.getUser(); // 댓글 작성자

        // 댓글 작성자==게시글 작성자일 때는 알림 생성 X
        if (!postOwner.getId().equals(commentAuthor.getId())) {
            Notification notification = new Notification();
            notification.setUser(postOwner);
            notification.setActorUserId(commentAuthor.getId());
            notification.setType(Notification.Type.COMMENT);
            notification.setTargetId(comment.getId()); // 해당 댓글 commentId
            notification.setRead(false);

            notificationRepository.save(notification);
        }
    }
}
