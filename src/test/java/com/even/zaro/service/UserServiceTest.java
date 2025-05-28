package com.even.zaro.service;

import com.even.zaro.dto.user.UpdatePasswordRequestDto;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
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

        static User createTestUser(Status status, String password) {
            return User.builder()
                    .id(1L)
                    .status(status)
                    .password(password)
                    .build();
        }

        @BeforeEach
        void setUp() {
            user = createTestUser(Status.ACTIVE, "OldEncoded1!");
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
    }
}