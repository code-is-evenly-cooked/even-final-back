package com.even.zaro.group;

import com.even.zaro.controller.GroupController;
import com.even.zaro.dto.group.GroupResponse;
import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.repository.FavoriteGroupRepository;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.GroupService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class GroupAPITest {

    @Autowired
    GroupService groupService;


    // repository --------
    @Autowired
    UserRepository userRepository;

    @Autowired
    FavoriteGroupRepository favoriteGroupRepository;

    @Test
    void 해당_사용자의_그룹리스트_조회_성공테스트() {

        // Given
        User user = createUser();

        // 즐겨찾기 그룹 예시 데이터
        FavoriteGroup group1 = FavoriteGroup.builder().user(user).name("맛집 모음").build();
        FavoriteGroup group2 = FavoriteGroup.builder().user(user).name("데이트 코스").build();
        FavoriteGroup group3 = FavoriteGroup.builder().user(user).name("가보고 싶은 곳").build();

        favoriteGroupRepository.saveAll(List.of(group1, group2, group3));// 한꺼번에 그룹 리스트 저장

        // When
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId());

        // Then
        assertThat(favoriteGroups.size()).isEqualTo(3); // 개수 검증
        assertThat(favoriteGroups.stream().map(GroupResponse::getName) // 그룹 이름 리스트 검증
                .toList()).containsExactlyInAnyOrder("맛집 모음", "데이트 코스", "가보고 싶은 곳");
    }

    @Test
    void 사용자의_그룹추가_성공테스트() {

        //
    }



    // 임시 유저 생성 메서드
    User createUser() {
        return userRepository.save(User.builder()
                .email("test@example.com")
                .password("Password1234!")
                .nickname("테스트유저")
                .provider(Provider.LOCAL)
                .status(Status.PENDING)
                .build());
    }



}
