package com.even.zaro.group;

import com.even.zaro.dto.group.GroupCreateRequest;
import com.even.zaro.dto.group.GroupEditRequest;
import com.even.zaro.dto.group.GroupResponse;
import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.group.GroupException;
import com.even.zaro.repository.FavoriteGroupRepository;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.FavoriteService;
import com.even.zaro.service.GroupService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class GroupAPITest {

    @Autowired
    GroupService groupService;

    @Autowired
    FavoriteService favoriteService;

    // repository --------
    @Autowired
    UserRepository userRepository;

    @Autowired
    FavoriteGroupRepository favoriteGroupRepository;

    @Test
    void 해당_사용자의_그룹리스트_조회_성공_테스트() {

        // Given : 유저 객체 생성
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "자취왕");

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
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "자취왕");

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
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "자취왕");

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


    @Test
    void 사용자의_그룹수정_성공_테스트() {
        // Given
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "자취왕");
        groupService.createGroup(GroupCreateRequest.builder().name("원래 이름").build(), user.getId());

        long groupId = groupService.getFavoriteGroups(user.getId()).getFirst().getId();

        // When
        GroupEditRequest editRequest = GroupEditRequest.builder().name("수정된 이름").build();
        groupService.editGroup(groupId, editRequest, user.getId());

        // Then
        GroupResponse updatedGroup = groupService.getFavoriteGroups(user.getId())
                .stream()
                .filter(gr -> gr.getId() == groupId)
                .findFirst()
                .orElseThrow();

        assertThat(updatedGroup.getName()).isEqualTo("수정된 이름");
    }

    @Test
    void 존재하지_않는_그룹_조회시_GROUP_NOT_FOUND_예외_발생() {

        // When & Then : 아직 그룹을 생성하지 않은 유저에 대해서 그룹 조회 요청
        GroupException groupException = assertThrows(GroupException.class, () -> {
            favoriteService.getGroupItems(1);
        });

        assertThat(groupException.getErrorCode()).isEqualTo(ErrorCode.GROUP_NOT_FOUND);
    }

    @Test
    void 이미_삭제한_그룹_삭제시도_GROUP_ALREADY_DELETE() {
        // Given
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "자취왕");

        // 그룹 생성
        craeteFavoriteGroup(user.getId(), "의정부 맛집은 여기라던데~?");

            // 그룹 리스트를 조회하고 첫번째 그룹의 id를 저장
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId());
        long firstGroupId = favoriteGroups.getFirst().getId();

            // 삭제 요청
        groupService.deleteGroup(firstGroupId, user.getId());

        // When & Then
            // 이미 삭제된 그룹에 대해서 다시 한번 삭제 요청
        GroupException exception = assertThrows(GroupException.class, () -> {
            groupService.deleteGroup(firstGroupId, user.getId());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.GROUP_ALREADY_DELETE);
    }

    @Test
    void 이미_존재하는_그룹_이름_추가_시도_GROUP_ALREADY_EXIST() {
        // Given
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "자취왕");

        // 그룹 생성
        craeteFavoriteGroup(user.getId(), "의정부 맛집은 여기라던데~?");

        // When & Then : 이미 추가한 그룹이름으로 한번 더 추가
        GroupException exception = assertThrows(GroupException.class, () -> {
            craeteFavoriteGroup(user.getId(), "의정부 맛집은 여기라던데~?");
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.GROUP_ALREADY_EXIST);
    }

    @Test
    void 다른_사용자의_그룹_삭제_시도_UNAUTHORIZED_GROUP_DELETE() {

        // When
        User user1 = createUser("ehdgnstla@naver.com", "Test1234!", "자취왕");
        User user2 = createUser("tlaehdgns@naver.com", "Test1234!", "자취왕2");


        // 그룹 생성
        craeteFavoriteGroup(user1.getId(), "user1의 그룹");

        // 그룹 리스트를 조회하고 첫번째 그룹의 id를 저장
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user1.getId());
        long firstGroupId = favoriteGroups.getFirst().getId();

        // Given & Then
            // user2가 user1의 그룹 삭제 시도
        GroupException exception = assertThrows(GroupException.class, () -> {
            groupService.deleteGroup(firstGroupId, user2.getId());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_GROUP_DELETE);
    }

    @Test
    void 다른_사용자의_그룹_수정_시도_UNAUTHORIZED_GROUP_UPDATE() {

        // When
        User user1 = createUser("ehdgnstla@naver.com", "Test1234!", "자취왕");
        User user2 = createUser("tlaehdgns@naver.com", "Test1234!", "자취왕2");


        // 그룹 생성
        craeteFavoriteGroup(user1.getId(), "user1의 그룹");

        // 그룹 리스트를 조회하고 첫번째 그룹의 id를 저장
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user1.getId());
        long firstGroupId = favoriteGroups.getFirst().getId();

        // Given & Then
        // user2가 user1의 그룹 수정 시도
        GroupException exception = assertThrows(GroupException.class, () -> {
            GroupEditRequest editRequest = GroupEditRequest.builder().name("user2가 user1의 그룹이름 수정 시도").build();
            groupService.editGroup(firstGroupId, editRequest, user2.getId());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_GROUP_UPDATE);
    }



    // 임시 유저 생성 메서드
    User createUser(String email, String password, String nickname) {
        return userRepository.save(User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .status(Status.PENDING)
                .build());
    }

    // 그룹 추가 메서드
    void craeteFavoriteGroup(long userId, String groupName) {
        GroupCreateRequest request = GroupCreateRequest.builder().name(groupName).build();
        groupService.createGroup(request, userId);
    }
}
