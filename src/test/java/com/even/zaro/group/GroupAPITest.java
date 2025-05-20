package com.even.zaro.group;

import com.even.zaro.controller.GroupController;
import com.even.zaro.dto.group.GroupCreateRequest;
import com.even.zaro.dto.group.GroupResponse;
import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.group.GroupException;
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
import java.util.Optional;
import java.util.stream.Collectors;

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
    void 해당_사용자의_그룹리스트_조회_성공_테스트() {

        // Given : 유저 객체 생성
        User user = createUser();

        // 즐겨찾기 그룹 예시 데이터
        FavoriteGroup group1 = FavoriteGroup.builder().user(user).name("맛집 모음").build();
        FavoriteGroup group2 = FavoriteGroup.builder().user(user).name("데이트 코스").build();
        FavoriteGroup group3 = FavoriteGroup.builder().user(user).name("가보고 싶은 곳").build();

        favoriteGroupRepository.saveAll(List.of(group1, group2, group3));// 한꺼번에 그룹 리스트 저장

        // When : 유저의 그룹 리스트 조회 요청
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId());

        // Then : 그룹 리스트의 개수와 그룹 이름 일치 여부 검증
        assertThat(favoriteGroups.size()).isEqualTo(3); // 개수 검증
        assertThat(favoriteGroups.stream().map(GroupResponse::getName) // 그룹 이름 리스트 검증
                .toList()).containsExactlyInAnyOrder("맛집 모음", "데이트 코스", "가보고 싶은 곳");
    }

    @Test
    void 사용자의_그룹추가_성공_테스트() {

        // Given : User 객체와 request 생성
        User user = createUser();

        GroupCreateRequest request = GroupCreateRequest.builder().name("의정부 맛집은 여기라던데~?").build();

        // When : 그룹 생성 요청
        groupService.createGroup(request, user.getId());

        // Then : 그룹이 정상적으로 추가되었는지 확인
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId()); // 해당 유저의 아이디로 그룹 리스트를 조회

        assertThat(favoriteGroups.size()).isEqualTo(1); // 개수 검증
        assertThat(favoriteGroups.stream().map(GroupResponse::getName)).containsExactlyInAnyOrder("의정부 맛집은 여기라던데~?"); // 그룹 이름 일치 여부
    }

    @Test
    void 사용자의_그룹삭제_성공_테스트() {
        // Given : 유저 객체 생성, 그룹 3개 생성
        User user = createUser();

        // 여러개의 그룹 생성 요청 생성
        GroupCreateRequest request1 = GroupCreateRequest.builder().name("groupName1").build();
        GroupCreateRequest request2 = GroupCreateRequest.builder().name("groupName2").build();
        GroupCreateRequest request3 = GroupCreateRequest.builder().name("groupName3").build();

        groupService.createGroup(request1, user.getId());
        groupService.createGroup(request2, user.getId());
        groupService.createGroup(request3, user.getId());

        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId()); // 그룹 리스트 조회
        List<Long> favoriteGroupIds = favoriteGroups.stream().map(GroupResponse::getId).toList(); // 그룹 id 리스트
        long delete_id = favoriteGroupIds.getLast(); // 마지막 그룹의 id : groupName3

        // When : 마지막 그룹 삭제 요청
        groupService.deleteGroup(delete_id, user.getId());

        // Then : 해당 그룹의 is_deleted가 true로 변경되었는지 검증

        // 해당 그룹의 is_deleted 상태가 1인지 확인해야함.
        FavoriteGroup group = favoriteGroupRepository.findById(delete_id)
                .orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND));

        assertThat(group.isDeleted()).isTrue();
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
