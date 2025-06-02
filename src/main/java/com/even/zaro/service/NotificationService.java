package com.even.zaro.service;

import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import com.even.zaro.entity.Post;
import com.even.zaro.entity.Comment;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.comment.CommentException;
import com.even.zaro.global.exception.notification.NotificationException;
import com.even.zaro.global.exception.post.PostException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.NotificationRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.CommentRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        List<Notification> notifications = notificationRepository.findAllByUserOrderByCreatedAtDesc(user);

        return notifications.stream()
                .map(notification -> {
                    Notification.Type type = notification.getType();

                    Long actorId = notification.getActorUserId();
                    if (actorId == null) {
                        throw new NotificationException(ErrorCode.ACTOR_USER_NOT_FOUND);
                    }
                    User actor = userRepository.findById(actorId)
                            .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

                    NotificationDto.NotificationDtoBuilder builder = NotificationDto.builder()
                            .id(notification.getId())
                            .type(type)
                            .targetId(notification.getTargetId())
                            .isRead(notification.isRead())
                            .createdAt(notification.getCreatedAt())
                            .actorId(actor.getId())
                            .actorName(actor.getNickname())
                            .actorProfileImage(actor.getProfileImage());

                    // LIKE, COMMENT 타입은 게시글, 댓글 정보 추가
                    if (type == Notification.Type.LIKE || type == Notification.Type.COMMENT) {
                        Post post = postRepository.findById(notification.getTargetId())
                                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
                        builder.postId(post.getId())
                                .category(post.getCategory().name())
                                .thumbnailImage(post.getThumbnailImage());

                        if (type == Notification.Type.COMMENT) {
                            Comment comment = commentRepository.findById(notification.getTargetId())
                                    .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));
                            builder.comment(comment.getContent());
                        }
                    }

                    return builder.build();
                }).toList();
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead(); // isRead = true
    }

    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserIdAndIsReadFalse(userId);
        notifications.forEach(n -> {
            if (!n.isRead()) {
                n.setRead(true);
            }
        });
    }
}
