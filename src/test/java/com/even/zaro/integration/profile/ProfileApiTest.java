package com.even.zaro.integration.profile;

import com.even.zaro.dto.PageResponse;
import com.even.zaro.dto.profile.FollowerFollowingListDto;
import com.even.zaro.dto.profile.UserCommentDto;
import com.even.zaro.dto.profile.UserPostDto;
import com.even.zaro.dto.profile.UserProfileDto;
import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.profile.ProfileException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.CommentRepository;
import com.even.zaro.repository.PostLikeRepository;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ProfileApiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProfileService profileService;

    @Test
    void 유저가_쓴_게시물_목록_조회_성공() {
        // given
        User user = createUser("test@even.com", "user1");
        createPost(user, "제목1", "내용1");
        createPost(user, "제목2", "내용2");

        // when
        PageResponse<UserPostDto> posts = profileService.getUserPosts(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(posts.getContent()).hasSize(2);
    }

    @Test
    void 유저가_좋아요_누른_게시물_목록_조회_성공() {
        // given
        User user = createUser("test@even.com", "user1");
        Post post = createPost(user, "제목1", "내용1");

        postLikeRepository.save(PostLike.builder().user(user).post(post).build());

        // when & then
        List<UserPostDto> liked = profileService.getUserLikedPosts(user.getId(), PageRequest.of(0, 10)).getContent();
        assertThat(liked).hasSize(1);
    }

    @Test
    void 유저가_작성한_댓글_목록_조회_성공() {
        // given
        User user = createUser("test@even.com", "user1");
        Post post = createPost(user, "제목1", "내용1");
        createComment(user, post, "댓글내용입니다");

        // when
        List<UserCommentDto> comments = profileService.getUserComments(user.getId(), PageRequest.of(0, 10)).getContent();

        // then
        assertThat(comments).hasSize(1);
    }

    @Test
    void 로그인된_유저가_다른_유저_팔로우하기_성공() {
        // given
        User follower = createUser("test1@even.com", "user1");
        User followee = createUser("test2@even.com", "user2");

        // when
        profileService.followUser(follower.getId(), followee.getId());

        // then
        assertThat(profileService.getUserFollowings(follower.getId())).hasSize(1);
        assertThat(profileService.getUserFollowers(followee.getId())).hasSize(1);
    }

    @Test
    void 로그인된_유저가_다른_유저_언팔로우하기_성공() {
        // given
        User follower = createUser("test1@even.com", "user1");
        User followee = createUser("test2@even.com", "user2");

        // when
        profileService.followUser(follower.getId(), followee.getId());
        profileService.unfollowUser(follower.getId(), followee.getId());

        // then
        assertThat(profileService.getUserFollowings(follower.getId())).isEmpty();
        assertThat(profileService.getUserFollowers(followee.getId())).isEmpty();
    }

    @Test
    void 특정_유저의_팔로잉_목록_조회_성공() {
        // given
        User userA = createUser("a@naver.com", "userA");
        User userB = createUser("b@naver.com", "userB");
        User userC = createUser("c@naver.com", "userC");

        profileService.followUser(userA.getId(), userB.getId());
        profileService.followUser(userA.getId(), userC.getId());

        // when
        List<FollowerFollowingListDto> followings = profileService.getUserFollowings(userA.getId());

        // then
        assertThat(followings).hasSize(2);
        assertThat(followings.stream().map(FollowerFollowingListDto::getUserId))
                .containsExactlyInAnyOrder(userB.getId(), userC.getId());
    }

    @Test
    void 특정_유저의_팔로워_목록_조회_성공() {
        // given
        User userA = createUser("a@naver.com", "userA");
        User userB = createUser("b@naver.com", "userB");
        User userC = createUser("c@naver.com", "userC");

        profileService.followUser(userB.getId(), userA.getId());
        profileService.followUser(userC.getId(), userA.getId());

        // when
        List<FollowerFollowingListDto> followers = profileService.getUserFollowers(userA.getId());

        // then
        assertThat(followers).hasSize(2);
        assertThat(followers.stream().map(FollowerFollowingListDto::getUserId))
                .containsExactlyInAnyOrder(userB.getId(), userC.getId());
    }


    /******** 헬퍼 메서드 ******/

    private User createUser(String email, String nickname) {
        return userRepository.save(User.builder()
                .email(email)
                .password("encodedPassword")
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .status(Status.ACTIVE)
                .build());
    }

    private Post createPost(User user, String title, String content) {
        return postRepository.save(Post.builder()
                .user(user)
                .title(title)
                .content(content)
                .category(Post.Category.DAILY_LIFE)
                .tag(Post.Tag.TIPS)
                .build());
    }

    private void createComment(User user, Post post, String content) {
        commentRepository.save(Comment.builder()
                .user(user)
                .post(post)
                .content(content)
                .build());
    }
}
