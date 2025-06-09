package com.even.zaro.unit.service;

import com.even.zaro.dto.profile.UserProfileDto;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @InjectMocks
    private ProfileService profileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = createUser(1L, "test@even.com", "유저1닉");
    }

    @Test
    void 유저_기본_프로필_조회_성공() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserProfileDto dto = profileService.getUserProfile(1L);

        assertThat(dto.getUserId()).isEqualTo(1L);
        assertThat(dto.getNickname()).isEqualTo("유저1닉");
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
