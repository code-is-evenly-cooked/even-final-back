package com.even.zaro.service;

import com.even.zaro.dto.UserPostDto;
import com.even.zaro.dto.UserProfileDto;
import com.even.zaro.dto.profileDto.GroupCreateRequest;
import com.even.zaro.dto.profileDto.GroupEditRequest;
import com.even.zaro.dto.profileDto.GroupResponse;
import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import com.even.zaro.global.exception.favoriteGroupEx.FavoriteGroupException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.FavoriteGroupRepository;
import com.even.zaro.repository.FavoriteRepository;
import com.even.zaro.repository.PlaceRepository;
import com.even.zaro.repository.PostLikeRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class ProfileService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteGroupRepository favoriteGroupRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    // 유저 기본 프로필 조회
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_RESULT));

        int postCount = postRepository.countByUserAndIsDeletedFalse(user);

        return UserProfileDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .liveAloneDate(user.getLiveAloneDate())
                .mbti(user.getMbti())
                .postCount(postCount)
                .followingCount(user.getFollowingCount())
                .followerCount(user.getFollowerCount())
                .build();
    }

    // 유저가 쓴 게시물 list 조회
    public Page<UserPostDto> getUserPosts(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_RESULT));

        return postRepository.findByUserAndIsDeletedFalse(user, pageable)
                .map(post -> UserPostDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .category(post.getCategory().name())
                        .tag(post.getTag() != null ? post.getTag().name() : null)
                        .imageUrl(post.getImageUrl())
                        .likeCount(post.getLikeCount())
                        .commentCount(post.getCommentCount())
                        .createdAt(post.getCreatedAt())
                        .build());
    }

    // 유저가 좋아요 누른 게시물 list 조회
    public Page<UserPostDto> getUserLikedPosts(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_RESULT));

        return postLikeRepository.findByUser(user, pageable)
                .map(postLike -> UserPostDto.builder()
                        .postId(postLike.getPost().getId())
                        .title(postLike.getPost().getTitle())
                        .content(postLike.getPost().getContent())
                        .category(postLike.getPost().getCategory().name())
                        .tag(postLike.getPost().getTag() != null ? postLike.getPost().getTag().name() : null)
                        .imageUrl(postLike.getPost().getImageUrl())
                        .likeCount(postLike.getPost().getLikeCount())
                        .commentCount(postLike.getPost().getCommentCount())
                        .createdAt(postLike.getPost().getCreatedAt())
                        .build());
    }
  
    public void createGroup(GroupCreateRequest request) {

        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserException(ErrorCode.EXAMPLE_USER_NOT_FOUND));

        boolean dupCheck = groupNameDuplicateCheck(request.getName(), request.getUserId());

        // 해당 유저가 이미 있는 그룹 이름을 입력했을 때
        if (dupCheck) {
            throw FavoriteGroupException.DuplicateGroupException();
        }

        FavoriteGroup favoriteGroup = FavoriteGroup.builder().user(user) // 유저 설정
                .name(request.getName()) // Group 이름 설정
                .updatedAt(LocalDateTime.now()).build();

        favoriteGroupRepository.save(favoriteGroup);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getFavoriteGroups(long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.EXAMPLE_USER_NOT_FOUND));


        // userId 값이 일치하는 데이터 조회
        List<FavoriteGroup> groupList = favoriteGroupRepository.findByUser(user);


        // GroupResponse 리스트로 변환
        List<GroupResponse> responseList = groupList.stream().map(group -> GroupResponse.builder().id(group.getId()).name(group.getName()).build()).toList();

        return responseList;
    }

    public void deleteGroup(long groupId) {
        FavoriteGroup group = favoriteGroupRepository.findById(groupId).orElseThrow(FavoriteGroupException::NotFoundGroupException);

        // 이미 삭제 처리 된 경우
        if (group.isDeleted()) {
            throw FavoriteGroupException.AlreadyDeletedGroupException();
        }

        group.setDeleted(true);

        favoriteGroupRepository.save(group);
    }
  
    public void editGroup(GroupEditRequest request) {
        FavoriteGroup group = favoriteGroupRepository.findById(request.getGroupId()).orElseThrow(FavoriteGroupException::NotFoundGroupException);

        boolean dupCheck = groupNameDuplicateCheck(group.getName(), group.getUser().getId());

        // 해당 유저가 이미 있는 그룹 이름을 입력했을 때
        if (dupCheck) {
            throw FavoriteGroupException.DuplicateGroupException();
        }


        group.setName(request.getName());
        group.setUpdatedAt(LocalDateTime.now());


        favoriteGroupRepository.save(group);
    }


    // 입력한 그룹 이름이 이미 해당 userId가 가지고 있는지 확인
    public boolean groupNameDuplicateCheck(String groupName, long userId) {
        return favoriteGroupRepository.existsByUser_IdAndName(userId, groupName);
    }
}
