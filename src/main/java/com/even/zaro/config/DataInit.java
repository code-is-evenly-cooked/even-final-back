package com.even.zaro.config;

import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInit {
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        if(userRepository.count() == 0) {
            User user = User.builder()
                    .email("test@example.com")
                    .nickname("테스트유저")
                    .password("encodedPassword")
                    .status(Status.ACTIVE)
                    .build();
            userRepository.save(user);
        }
    }
}
