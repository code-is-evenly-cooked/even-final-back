package com.even.zaro.service;

import com.even.zaro.dto.user.UpdateNicknameRequestDto;
import com.even.zaro.dto.user.UpdateNicknameResponseDto;
import com.even.zaro.dto.user.UpdatePasswordRequestDto;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    class updateNicknameTest {
        private User user;

        static User createTestUser() {
            return User.builder()
                    .id(1L)
                    .status(Status.ACTIVE)
                    .nickname("기존닉네임")
                    .build();
        }

        @BeforeEach
        void setUp() {
            user = createTestUser();
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        }

        @Test
        void successfully_updated_nickname() {
            UpdateNicknameRequestDto requestDto = new UpdateNicknameRequestDto("새닉네임");

            UpdateNicknameResponseDto responseDto = userService.updateNickname(1L, requestDto);

            assertEquals("새닉네임", responseDto.getNickname());
            assertNotNull(user.getLastNicknameUpdatedAt());
        }

        @Test
        void shouldThrowException_whenUserDoesNotExist() {
            UpdateNicknameRequestDto requestDto = new UpdateNicknameRequestDto("새닉네임");
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            UserException ex = assertThrows(UserException.class, () -> userService.updateNickname(1L, requestDto));

            assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        void shouldThrowException_whenUserIsNotVerified() {
            user.changeStatus(Status.PENDING);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            UpdateNicknameRequestDto requestDto = new UpdateNicknameRequestDto("새닉네임");

            UserException ex = assertThrows(UserException.class, () -> userService.updateNickname(1L, requestDto));

            assertEquals(ErrorCode.MAIL_NOT_VERIFIED, ex.getErrorCode());
        }

        @Test
        void shouldThrowException_whenNewNicknameIsBlank() {
            UpdateNicknameRequestDto requestDto = new UpdateNicknameRequestDto("");

            UserException ex = assertThrows(UserException.class, () -> userService.updateNickname(1L, requestDto));

            assertEquals(ErrorCode.NEW_NICKNAME_REQUIRED, ex.getErrorCode());
        }

        @Test
        void shouldThrowException_whenNicknameFormatIsInvalid() {
            UpdateNicknameRequestDto requestDto = new UpdateNicknameRequestDto("형식파괴!!!");

            UserException ex = assertThrows(UserException.class, () -> userService.updateNickname(1L, requestDto));

            assertEquals(ErrorCode.INVALID_NICKNAME_FORMAT, ex.getErrorCode());
        }

        @Test
        void shouldThrowException_whenNicknameAlreadyExists() {
            UpdateNicknameRequestDto requestDto = new UpdateNicknameRequestDto("이미있니");
            when(userRepository.existsByNickname("이미있니")).thenReturn(true);

            UserException ex = assertThrows(UserException.class, () -> userService.updateNickname(1L, requestDto));

            assertEquals(ErrorCode.NICKNAME_ALREADY_EXISTED, ex.getErrorCode());
        }

        @Test
        void shouldThrowException_whenNewNicknameIsSameAsOriginalOne() {
            UpdateNicknameRequestDto requestDto = new UpdateNicknameRequestDto("기존닉네임");

            UserException ex = assertThrows(UserException.class, () -> userService.updateNickname(1L, requestDto));

            assertEquals(ErrorCode.NEW_NICKNAME_EQUALS_ORIGINAL_NICKNAME, ex.getErrorCode());
        }

        @Test
        void shouldThrowException_whenNicknameChangedWithinRestrictionPeriod() {
            user.updateLastNicknameUpdatedAt(LocalDateTime.now().minusDays(13));
            UpdateNicknameRequestDto requestDto = new UpdateNicknameRequestDto("새닉네임");

            UserException ex = assertThrows(UserException.class, () -> userService.updateNickname(1L, requestDto));

            assertEquals(ErrorCode.NICKNAME_UPDATE_COOLDOWN, ex.getErrorCode());
        }
    }

    @Nested
    class updatePasswordTest {

        private User user;

        static User createTestUser() {
            return User.builder()
                    .id(1L)
                    .status(Status.ACTIVE)
                    .password("OldEncoded1!")
                    .build();
        }

        @BeforeEach
        void setUp() {
            user = createTestUser();
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        }


        @Test
        void successfully_password_updated() {
            //g
            UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("Old1234!", "New1234!");
            when(passwordEncoder.matches("Old1234!", "OldEncoded1!")).thenReturn(true);
            when(passwordEncoder.encode("New1234!")).thenReturn("NewEncoded1!");

            //w
            userService.updatePassword(1L, requestDto);

            //t
            assertEquals("NewEncoded1!", user.getPassword());
        }

        @Test
        void shouldThrowException_whenUserIsNotVerified() {
            user.changeStatus(Status.PENDING);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("Old1234!", "New1234!");

            UserException ex = assertThrows(UserException.class, () -> userService.updatePassword(1L, requestDto));

            assertEquals(ErrorCode.MAIL_NOT_VERIFIED, ex.getErrorCode());
        }

        @Test
        void shouldThrowException_whenCurrentPasswordIsBlank() {
            UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("", "New1234!");

            UserException ex = assertThrows(UserException.class, () -> userService.updatePassword(1L, requestDto));

            assertEquals(ErrorCode.CURRENT_PASSWORD_REQUIRED, ex.getErrorCode());
        }

        @Test
        void shouldThrowException_whenCurrentPasswordIsIncorrect() {
            UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("ThisIsWrong1!", "New1234!");
            when(passwordEncoder.matches("ThisIsWrong1!", "OldEncoded1!")).thenReturn(false);

            UserException ex = assertThrows(UserException.class, () -> userService.updatePassword(1L, requestDto));

            assertEquals(ErrorCode.CURRENT_PASSWORD_WRONG, ex.getErrorCode());
        }

        @Test
        void shouldThrowException_whenNewPasswordIsBlank() {
            UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("Old1234!", "");
            when(passwordEncoder.matches("Old1234!", "OldEncoded1!")).thenReturn(true);

            UserException ex = assertThrows(UserException.class, () -> userService.updatePassword(1L, requestDto));

            assertEquals(ErrorCode.PASSWORD_REQUIRED, ex.getErrorCode());
        }

        @Test
        void shouldThrowException_whenNewPasswordFormatIsInvalid() {
            UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("Old1234!", "wrongFormat");
            when(passwordEncoder.matches("Old1234!", "OldEncoded1!")).thenReturn(true);

            UserException ex = assertThrows(UserException.class, () -> userService.updatePassword(1L, requestDto));

            assertEquals(ErrorCode.INVALID_PASSWORD_FORMAT, ex.getErrorCode());
        }

        @Test
        void shouldThrowException_whenCurrentPasswordIsSameAsNewPassword() {
            UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("Old1234!", "Old1234!");
            when(passwordEncoder.matches("Old1234!", "OldEncoded1!")).thenReturn(true);

            UserException ex = assertThrows(UserException.class, () -> userService.updatePassword(1L, requestDto));

            assertEquals(ErrorCode.CURRENT_PASSWORD_EQUALS_NEW_PASSWORD, ex.getErrorCode());
        }
    }
}