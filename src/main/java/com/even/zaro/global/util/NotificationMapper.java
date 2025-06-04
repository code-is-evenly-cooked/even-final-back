package com.even.zaro.global.util;

import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.comment.CommentException;
import com.even.zaro.global.exception.notification.NotificationException;
import com.even.zaro.global.exception.post.PostException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.CommentRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public NotificationDto toDto(Notification notification) {
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

        if (type == Notification.Type.LIKE) {
            Post post = postRepository.findById(notification.getTargetId())
                    .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
            builder.postId(post.getId())
                    .category(post.getCategory().name())
                    .thumbnailImage(post.getThumbnailImage());
        }

        if (type == Notification.Type.COMMENT) {
            Comment comment = commentRepository.findById(notification.getTargetId())
                    .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));
            Post post = comment.getPost();
            builder.comment(comment.getContent())
                    .postId(post.getId())
                    .category(post.getCategory().name())
                    .thumbnailImage(post.getThumbnailImage());
        }

        return builder.build();
    }
}
