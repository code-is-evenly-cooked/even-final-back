package com.even.zaro.integration.notification;

import com.even.zaro.entity.*;
import com.even.zaro.repository.*;
import com.even.zaro.dto.notification.NotificationDto;
import com.even.zaro.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class NotificationIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @DisplayName("팔로우 알림 생성 후 조회 시 DTO 정상 반환")
    @Test
    void 팔로우_알림_생성_및_조회_성공() {
        User follower = createUser("follower@even.com", "팔로워닉");
        User followee = createUser("followee@even.com", "팔로위닉");

        Follow follow = Follow.builder().follower(follower).followee(followee).build();
        notificationService.createFollowNotification(follow);

        List<NotificationDto> list = notificationService.getNotificationsList(followee.getId());
        NotificationDto dto = list.get(0);

        assertThat(dto.getType()).isEqualTo(Notification.Type.FOLLOW);
        assertThat(dto.getTargetId()).isEqualTo(follower.getId());
        assertThat(dto.getActorName()).isEqualTo("팔로워닉");
    }

    @DisplayName("좋아요 알림 생성 후 조회 시 DTO 정상 반환")
    @Test
    void 좋아요_알림_생성_및_조회_성공() {
        User liker = createUser("liker@even.com", "좋아요닉");
        User owner = createUser("owner@even.com", "게시글주인");

        Post post = createPost(owner);
        PostLike postLike = PostLike.builder().user(liker).post(post).build();

        notificationService.createPostLikeNotification(postLike);

        List<NotificationDto> list = notificationService.getNotificationsList(owner.getId());
        NotificationDto dto = list.get(0);

        assertThat(dto.getType()).isEqualTo(Notification.Type.LIKE);
        assertThat(dto.getPostId()).isEqualTo(post.getId());
        assertThat(dto.getCategory()).isEqualTo("TOGETHER");
    }

    @DisplayName("댓글 알림 생성 후 조회 시 DTO 정상 반환")
    @Test
    void 댓글_알림_생성_및_조회_성공() {
        User commenter = createUser("commenter@even.com", "댓글러닉");
        User owner = createUser("owner@even.com", "게시글주인");

        Post post = createPost(owner);
        Comment comment = commentRepository.save(Comment.builder()
                .user(commenter)
                .post(post)
                .content("댓글내용")
                .build());

        notificationService.createCommentNotification(comment);

        List<NotificationDto> list = notificationService.getNotificationsList(owner.getId());
        NotificationDto dto = list.get(0);

        assertThat(dto.getType()).isEqualTo(Notification.Type.COMMENT);
        assertThat(dto.getComment()).isEqualTo("댓글내용");
        assertThat(dto.getPostId()).isEqualTo(post.getId());
        assertThat(dto.getCategory()).isEqualTo("TOGETHER");
        assertThat(dto.getActorId()).isEqualTo(commenter.getId());
        assertThat(dto.getActorName()).isEqualTo(commenter.getNickname());
    }


    private User createUser(String email, String nickname) {
        return userRepository.save(User.builder()
                .email(email)
                .password("password")
                .nickname(nickname)
                .profileImage(null)
                .provider(Provider.LOCAL)
                .status(Status.ACTIVE)
                .build());
    }

    private Post createPost(User owner) {
        return postRepository.save(Post.builder()
                .user(owner)
                .category(Post.Category.TOGETHER)
                .tag(Post.Tag.GROUP_BUY)
                .thumbnailImage(null)
                .title("게시글제목")
                .content("게시글내용임다")
                .build());
    }
}
