package com.even.zaro.service;

import com.even.zaro.dto.auth.SignUpRequestDto;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("회원가입시 닉네임 중복 예외처리")
    @Test
    void nicknameAlreadyExists_shouldThrowException() {
        //given
        when(userRepository.existsByNickname("이브니")).thenReturn(true);

        //when
        SignUpRequestDto requestDto = new SignUpRequestDto("test2@even.com", "Test1234!", "이브니");

        //then
        UserException userException = assertThrows(UserException.class, () -> authService.signUp(requestDto));
        assertEquals(ErrorCode.NICKNAME_ALREADY_EXISTED, userException.getErrorCode());
    }
}