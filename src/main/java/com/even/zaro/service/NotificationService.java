package com.even.zaro.service;

import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.notification.NotificationException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.global.util.NotificationMapper;
import com.even.zaro.repository.NotificationRepository;
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
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationSseService notificationSseService;

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        List<Notification> notifications = notificationRepository.findAllByUserOrderByCreatedAtDesc(user);

        return notifications.stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Transactional
    public void createCommentNotification(Comment comment) {
        User postOwner = comment.getPost().getUser(); // 게시글 작성자
        User commentAuthor = comment.getUser(); // 댓글 작성자

        // 댓글 작성자==게시글 작성자일 때는 알림 생성 X
        if (postOwner.getId().equals(commentAuthor.getId())) return;

        Notification notification = new Notification();
        notification.setUser(postOwner);
        notification.setActorUserId(commentAuthor.getId());
        notification.setType(Notification.Type.COMMENT);
        notification.setTargetId(comment.getId());
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);

        // sse 실시간 전송
        notificationSseService.send(postOwner.getId(), saved);
    }

    @Transactional
    public void createPostLikeNotification(PostLike postLike) {
        User postOwner = postLike.getPost().getUser(); // 게시물 작성자
        User likeUser = postLike.getUser(); // 좋아요 누른 유저

        // 댓글 작성자==좋아요 누른 유저 일 때는 알림 생성 X
        if (!postOwner.getId().equals(likeUser.getId())) {
            Notification notification = new Notification();
            notification.setUser(postOwner);
            notification.setActorUserId(likeUser.getId());
            notification.setType(Notification.Type.LIKE);
            notification.setTargetId(postLike.getPost().getId());
            notification.setRead(false);

            Notification saved = notificationRepository.save(notification);

            // sse 실시간 전송
            notificationSseService.send(postOwner.getId(), saved);
        }
    }

    @Transactional
    public void createFollowNotification(Follow follow) {
        User followee = follow.getFollowee(); // 팔로우 당한 사용자 (알림 대상)
        User follower = follow.getFollower(); // 팔로우 한 사용자

        Notification notification = new Notification();
        notification.setUser(followee);
        notification.setActorUserId(follower.getId());
        notification.setType(Notification.Type.FOLLOW);
        notification.setTargetId(follower.getId()); // 팔로우 한 사용자 userId
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);

        // sse 실시간 전송
        notificationSseService.send(followee.getId(), saved);
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
