package com.even.zaro.service;

import com.even.zaro.dto.profileDto.GroupCreateRequest;
import com.even.zaro.dto.profileDto.GroupEditRequest;
import com.even.zaro.dto.profileDto.GroupResponse;
import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.User;
import com.even.zaro.global.exception.favoriteGroupEx.FavoriteGroupException;
import com.even.zaro.global.exception.userEx.UserException;
import com.even.zaro.repository.FavoriteGroupRepository;
import com.even.zaro.repository.FavoriteRepository;
import com.even.zaro.repository.PlaceRepository;
import com.even.zaro.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProfileService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteGroupRepository favoriteGroupRepository;

    @Transactional
    public void createGroup(GroupCreateRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(UserException::NotFoundUserException);

        FavoriteGroup favoriteGroup = FavoriteGroup.builder()
                .user(user) // 유저 설정
                .name(request.getName()) // Group 이름 설정
                .updatedAt(LocalDateTime.now())
                .build();

        favoriteGroupRepository.save(favoriteGroup);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getFavoriteGroups(long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserException::NotFoundUserException);


        // userId 값이 일치하는 데이터 조회
        List<FavoriteGroup> groupList = favoriteGroupRepository.findByUser(user);


        // GroupResponse 리스트로 변환
        List<GroupResponse> responseList = groupList.stream()
                .map(group -> GroupResponse.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .build())
                .toList();

        return responseList;
    }

    @Transactional
    public void deleteGroup(long groupId) {
        FavoriteGroup group = favoriteGroupRepository.findById(groupId).orElseThrow(FavoriteGroupException::NotFoundGroupException);

        // 이미 삭제 처리 된 경우
        if(group.isDeleted()) {
            throw FavoriteGroupException.AlreadyDeletedGroupException();
        }

        group.setDeleted(true);

        favoriteGroupRepository.save(group);
    }

    @Transactional
    public void editGroup(GroupEditRequest request) {
        FavoriteGroup group = favoriteGroupRepository.findById(request.getGroupId())
                .orElseThrow(FavoriteGroupException::NotFoundGroupException);

        group.setName(request.getName());
        group.setUpdatedAt(LocalDateTime.now());


        favoriteGroupRepository.save(group);
    }
}
