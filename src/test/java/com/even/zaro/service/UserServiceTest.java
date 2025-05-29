package com.even.zaro.service;

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