package com.even.zaro.global.converter;

import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EnumConversionPerformanceTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void ProviderEnumTest() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            User user = User.builder()
                    .email("string" + i + "@test.com")
                    .nickname("user" + i)
                    .provider(Provider.LOCAL)
                    .status(Status.ACTIVE)
                    .build();
            userRepository.save(user);
        }

        long end = System.currentTimeMillis();
        System.out.println("소요 시간: " + (end - start) + "ms");
    }
}
