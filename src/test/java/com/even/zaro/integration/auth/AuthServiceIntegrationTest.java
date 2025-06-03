package com.even.zaro.integration.auth;

import com.even.zaro.dto.auth.SignUpRequestDto;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void nicknameAlreadyExists_shouldThrowException() {
        //given
        User user = User.builder()
                .email("test@even.com")
                .nickname("이브니")
                .password("Password1!")
                .provider(Provider.LOCAL)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(user);

        //when
        SignUpRequestDto requestDto = new SignUpRequestDto("test2@even.com", "Test1234!", "이브니");

        //then
        UserException userException = assertThrows(UserException.class, () -> authService.signUp(requestDto));
        assertEquals(ErrorCode.NICKNAME_ALREADY_EXISTED, userException.getErrorCode());
    }
}