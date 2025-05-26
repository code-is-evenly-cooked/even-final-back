package com.even.zaro.profile;

import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ProfileApiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileService profileService;
}
