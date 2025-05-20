package com.even.zaro.group;

import com.even.zaro.controller.GroupController;
import com.even.zaro.repository.FavoriteGroupRepository;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class GroupAPITest {

    @Autowired
    GroupController groupController;

    @Autowired
    GroupService groupService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FavoriteGroupRepository favoriteGroupRepository;

}
