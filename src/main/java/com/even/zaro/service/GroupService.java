package com.even.zaro.service;

import com.even.zaro.dto.group.GroupCreateRequest;
import com.even.zaro.dto.group.GroupEditRequest;
import com.even.zaro.dto.group.GroupResponse;
import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.group.GroupException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.FavoriteGroupRepository;
import com.even.zaro.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class GroupService {
    private final UserRepository userRepository;
    private final FavoriteGroupRepository favoriteGroupRepository;

    public void createGroup(GroupCreateRequest request, long userid) {

        User user = userRepository.findById(userid).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        boolean dupCheck = groupNameDuplicateCheck(request.getGroupName(), userid);

        // 해당 유저가 이미 있는 그룹 이름을 입력했을 때
        if (dupCheck) {
            throw new GroupException(ErrorCode.GROUP_ALREADY_EXIST);
        }

        FavoriteGroup favoriteGroup = FavoriteGroup.builder()
                .user(user) // 유저 설정
                .name(request.getGroupName()) // Group 이름 설정
                .build();

        favoriteGroupRepository.save(favoriteGroup);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getFavoriteGroups(long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        // userId 값이 일치하는 데이터 조회
        List<FavoriteGroup> groupList = favoriteGroupRepository.findByUser(user);

        if (groupList.isEmpty()) {
            throw new GroupException(ErrorCode.GROUP_LIST_NOT_FOUND);
        }


        // GroupResponse 리스트로 변환
        List<GroupResponse> responseList = groupList.stream().map(group ->
                        GroupResponse.builder()
                                .groupId(group.getId())
                                .name(group.getName())
                                .isDeleted(group.isDeleted())
                                .createdAt(group.getCreatedAt())
                                .updatedAt(group.getUpdatedAt())
                                .build())
                .toList();

        return responseList;
    }

    public void deleteGroup(long groupId, long userId) {
        FavoriteGroup group = favoriteGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND));

        // 다른 사용자의 그룹 삭제 방지
        if (group.getUser().getId() != userId) {
            throw new GroupException(ErrorCode.UNAUTHORIZED_GROUP_DELETE);
        }

        // 이미 삭제 처리 된 경우
        if (group.isDeleted()) {
            throw new GroupException(ErrorCode.GROUP_ALREADY_DELETE);
        }

        group.setIsDeleted();

        favoriteGroupRepository.save(group);
    }

    public void editGroup(long groupId, GroupEditRequest request, long userId) {
        FavoriteGroup group = favoriteGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND));

        // 다른 사용자의 즐겨찾기 그룹 수정 방지
        if (group.getUser().getId() != userId) {
            throw new GroupException(ErrorCode.UNAUTHORIZED_GROUP_UPDATE);
        }

        boolean dupCheck = groupNameDuplicateCheck(request.getGroupName(), group.getUser().getId());

        // 해당 유저가 이미 있는 즐겨찾기 그룹 이름을 입력했을 때
        if (dupCheck) {
            throw new GroupException(ErrorCode.GROUP_ALREADY_EXIST);
        }
        group.setName(request.getGroupName());
    }


    // 입력한 그룹 이름이 이미 해당 userId가 가지고 있는지 확인
    public boolean groupNameDuplicateCheck(String groupName, long userId) {
        return favoriteGroupRepository.existsByUser_IdAndName(userId, groupName);
    }
}
