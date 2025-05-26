package com.even.zaro.profile;

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
    public void 유저_기본_프로필_조회_성공() {
        // given
        User user = User.builder()
                .email("test@naver.com")
                .nickname("닉네임")
                .profileImage("/images/profile/2-uuid.png")
                .provider(com.even.zaro.entity.Provider.LOCAL)
                .status(com.even.zaro.entity.Status.ACTIVE)
                .build();
        userRepository.save(user);

        // when
        var result = profileService.getUserProfile(user.getId());

        // then
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    void 존재하지_않는_유저_프로필_조회_실패() {
        // when & then
        UserException exception = assertThrows(UserException.class, () ->
                profileService.getUserProfile(9999L));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}
