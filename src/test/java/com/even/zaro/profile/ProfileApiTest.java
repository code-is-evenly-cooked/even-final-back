package com.even.zaro.profile;

import com.even.zaro.dto.profile.UserProfileDto;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ProfileApiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileService profileService;

    @Test
    void 유저_기본_프로필_조회_성공() {
        // given
        User user = createUser("test@naver.com", "닉네임");

        // when
        UserProfileDto profile = profileService.getUserProfile(user.getId());

        // then
        assertThat(profile.getUserId()).isEqualTo(user.getId());
        assertThat(profile.getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    void 유저가_쓴_게시물_목록_조회_성공() {

    }

    @Test
    void 유저가_좋아요_누른_게시물_목록_조회_성공() {

    }

    @Test
    void 유저가_작성한_댓글_목록_조회_성공() {

    }

    @Test
    void 로그인된_유저가_다른_유저_팔로우하기_성공() {

    }

    @Test
    void 로그인된_유저가_다른_유저_언팔로우하기_성공() {

    }

    @Test
    void 특정_유저의_팔로잉_목록_조회_성공() {

    }

    @Test
    void 특정_유저의_팔로워_목록_조회_성공() {

    }


    @Test
    void 존재하지_않는_유저_프로필_조회_실패() {
        // when & then
        UserException exception = assertThrows(UserException.class, () ->
                profileService.getUserProfile(9999L));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    private User createUser(String email, String nickname) {
        return userRepository.save(User.builder()
                .email(email)
                .password("encodedPassword")
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .status(Status.ACTIVE)
                .build());
    }
}
