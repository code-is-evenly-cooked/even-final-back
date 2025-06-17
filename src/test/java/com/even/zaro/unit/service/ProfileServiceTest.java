package com.even.zaro.unit.service;

import com.even.zaro.dto.profile.UserProfileDto;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.profile.ProfileException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.FollowRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.service.ProfileService;
import com.even.zaro.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @InjectMocks
    private ProfileService profileService;

    @Mock
    private UserService userService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private FollowRepository followRepository;

    @Nested
    class GetUserProfileTest {

        private User user;

        @BeforeEach
        void setUp() {
            user = createUser(1L, "test@even.com", "유저1닉");
        }

        @Test
        void 유저_기본_프로필_조회_성공() {
            when(userService.findActiveUserById(1L)).thenReturn(user);
            when(postRepository.countByUserAndIsDeletedFalse(user)).thenReturn(3);

            UserProfileDto dto = profileService.getUserProfile(1L, null);

            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getNickname()).isEqualTo("유저1닉");
            assertThat(dto.getPostCount()).isEqualTo(3);
        }

        @Test
        void 존재하지_않는_유저_프로필_조회_실패() {
            when(userService.findActiveUserById(9999L)).thenThrow(new UserException(ErrorCode.USER_NOT_FOUND));

            UserException exception = assertThrows(UserException.class, () ->
                    profileService.getUserProfile(9999L, null));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    class FollowTest {

        private User follower;
        private User followee;

        @BeforeEach
        void setUp() {
            follower = createUser(1L, "follower@even.com", "follower");
            followee = createUser(2L, "followee@even.com", "followee");
        }

        @Test
        void 자기자신을_팔로우_시도_실패() {
            ProfileException exception = assertThrows(ProfileException.class, () ->
                    profileService.followUser(follower.getId(), follower.getId()));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FOLLOW_SELF_NOT_ALLOWED);
        }

        @Test
        void 이미_팔로우한_유저_중복_팔로우_실패() {
            when(userService.findUserById(follower.getId())).thenReturn(follower);
            when(userService.findUserById(followee.getId())).thenReturn(followee);
            when(followRepository.existsByFollowerAndFollowee(follower, followee)).thenReturn(true);

            ProfileException exception = assertThrows(ProfileException.class, () ->
                    profileService.followUser(follower.getId(), followee.getId()));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FOLLOW_ALREADY_EXISTS);
        }

        @Test
        void 존재하지_않는_유저에게_팔로우_시도_실패() {
            when(userService.findUserById(2L))
                    .thenReturn(createUser(1L, "a@even.com", "A"));
            when(userService.findUserById(99999L)).thenThrow(new UserException(ErrorCode.USER_NOT_FOUND));

            UserException exception = assertThrows(UserException.class, () ->
                    profileService.followUser(2L, 99999L));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        void 자기자신을_언팔로우_시도_실패() {
            ProfileException exception = assertThrows(ProfileException.class, () ->
                    profileService.unfollowUser(follower.getId(), follower.getId()));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FOLLOW_UNFOLLOW_SELF_NOT_ALLOWED);
        }

        @Test
        void 존재하지_않는_팔로우관계_언팔로우_실패() {
            when(userService.findUserById(follower.getId())).thenReturn(follower);
            when(userService.findUserById(followee.getId())).thenReturn(followee);

            ProfileException exception = assertThrows(ProfileException.class, () ->
                    profileService.unfollowUser(follower.getId(), followee.getId()));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FOLLOW_NOT_EXIST);
        }
    }

    private User createUser(Long id, String email, String nickname) {
        return User.builder()
                .id(id)
                .email(email)
                .password("password")
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .status(Status.ACTIVE)
                .build();
    }
}
