package com.even.zaro.unit.service;

import com.even.zaro.dto.auth.SignUpRequestDto;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.global.jwt.JwtUtil;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

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

    @Test
    void signOut_shouldStoreAccessTokenInBlacklist() {
        String testAccessToken = "test-token";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(jwtUtil.getAccessTokenExpiration(anyString()))
                .thenReturn(new Date(System.currentTimeMillis() + 1000 * 60 * 15));

        authService.signOut(1L, testAccessToken);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(eq("BL:" + testAccessToken), eq("logout"), anyLong(), eq(TimeUnit.MILLISECONDS));
    }
}