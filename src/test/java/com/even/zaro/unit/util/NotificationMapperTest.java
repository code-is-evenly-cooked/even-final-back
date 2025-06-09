package com.even.zaro.unit.util;

import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.entity.Comment;
import com.even.zaro.entity.Notification;
import com.even.zaro.entity.Post;
import com.even.zaro.entity.User;
import com.even.zaro.global.util.NotificationMapper;
import com.even.zaro.repository.CommentRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationMapperTest {

    @InjectMocks
    private NotificationMapper notificationMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    private final Long userId = 1L;
    private final Long notificationId = 9L;

    private User actor;

    @BeforeEach
    void setUp() {
        actor = User.builder()
                .id(userId)
                .nickname("액터닉넴")
                .profileImage(null)
                .build();
    }

    @Test
    void FOLLOW_알림_DTO_변환_성공() {
        Notification noti = Notification.builder()
                .id(notificationId)
                .type(Notification.Type.FOLLOW)
                .targetId(2L)
                .actorUserId(actor.getId())
                .isRead(false)
                .build();

        when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));

        NotificationDto dto = notificationMapper.toDto(noti);

        assertThat(dto.getType()).isEqualTo(Notification.Type.FOLLOW);
        assertThat(dto.getActorId()).isEqualTo(actor.getId());
        assertThat(dto.getActorName()).isEqualTo(actor.getNickname());
    }

    @Test
    void LIKE_알림_DTO_변환_성공() {
        Post post = Post.builder()
                .id(10L)
                .category(Post.Category.TOGETHER)
                .thumbnailImage(null)
                .build();

        Notification noti = Notification.builder()
                .id(notificationId)
                .type(Notification.Type.LIKE)
                .targetId(post.getId())
                .actorUserId(actor.getId())
                .isRead(true)
                .build();

        when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        NotificationDto dto = notificationMapper.toDto(noti);

        assertThat(dto.getPostId()).isEqualTo(post.getId());
        assertThat(dto.getCategory()).isEqualTo("TOGETHER");
        assertThat(dto.getThumbnailImage()).isEqualTo(null);
    }

    @Test
    void COMMENT_알림_DTO_변환_성공() {
        Post post = Post.builder()
                .id(10L)
                .category(Post.Category.TOGETHER)
                .thumbnailImage(null)
                .build();

        Comment comment = Comment.builder()
                .id(20L)
                .content("댓글 내용")
                .post(post)
                .build();

        Notification noti = Notification.builder()
                .id(notificationId)
                .type(Notification.Type.COMMENT)
                .targetId(comment.getId())
                .actorUserId(actor.getId())
                .isRead(false)
                .build();

        when(userRepository.findById(actor.getId())).thenReturn(Optional.of(actor));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        NotificationDto dto = notificationMapper.toDto(noti);

        assertThat(dto.getComment()).isEqualTo("댓글 내용");
        assertThat(dto.getCategory()).isEqualTo("TOGETHER");
        assertThat(dto.getPostId()).isEqualTo(post.getId());
    }
}
