package com.even.zaro.service;

import com.even.zaro.dto.profile.*;
import com.even.zaro.dto.favorite.FavoriteAddRequest;
import com.even.zaro.dto.favorite.FavoriteAddResponse;
import com.even.zaro.dto.favorite.FavoriteEditRequest;
import com.even.zaro.dto.favorite.FavoriteResponse;
import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import com.even.zaro.global.exception.map.MapException;
import com.even.zaro.global.exception.profile.ProfileException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.*;
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
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;

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

    // 유저가 작성한 댓글 list 조회
    public Page<UserCommentDto> getUserComments(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_RESULT));

        return commentRepository.findByUserAndIsDeletedFalse(user, pageable)
                .map(comment -> {
                    Post post = comment.getPost();
                    if (post == null) {
                        throw new CustomException(ErrorCode.NO_RESULT);
                    }

                    return UserCommentDto.builder()
                            .postId(post.getId())
                            .title(post.getTitle())
                            .category(post.getCategory().name())
                            .tag(post.getTag() != null ? post.getTag().name() : null)
                            .likeCount(post.getLikeCount())
                            .commentCount(post.getCommentCount())
                            .commentContent(comment.getContent())
                            .commentCreatedAt(comment.getCreatedAt())
                            .build();
                });
    }

    ////////////// 팔로우 관련

    // 다른 유저 팔로우 하기
    public void followUser(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new CustomException(ErrorCode.INVALID_ARGUMENT);
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_RESULT));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_RESULT));

        // 이미 팔로우되어 있는지 확인
        boolean alreadyFollowing = followRepository.existsByFollowerAndFollowee(follower, followee);
        if (alreadyFollowing) {
            throw new CustomException(ErrorCode.INVALID_ARGUMENT);
        }

        // 팔로우 저장
        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();
        followRepository.save(follow);
    }

    // 다른 유저 언팔로우 하기
    public void unfollowUser(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new CustomException(ErrorCode.INVALID_ARGUMENT);
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_RESULT));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_RESULT));

        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_RESULT));

        followRepository.delete(follow);
    }

    // 팔로잉 목록 조회
    public List<FollowerFollowingListDto> getUserFollowings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_RESULT));

        return followRepository.findByFollower(user).stream()
                .map(follow -> FollowerFollowingListDto.builder()
                        .userId(follow.getFollowee().getId())
                        .userName(follow.getFollowee().getNickname())
                        .profileImage(follow.getFollowee().getProfileImage())
                        .build())
                .toList();
    }


    ////////////// 즐겨찾기

    public void createGroup(GroupCreateRequest request) {

        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserException(ErrorCode.EXAMPLE_USER_NOT_FOUND));

        boolean dupCheck = groupNameDuplicateCheck(request.getName(), request.getUserId());

        // 해당 유저가 이미 있는 그룹 이름을 입력했을 때
        if (dupCheck) {
            throw new ProfileException(ErrorCode.GROUP_ALREADY_EXIST);
        }

        FavoriteGroup favoriteGroup = FavoriteGroup.builder().user(user) // 유저 설정
                .name(request.getName()) // Group 이름 설정
                .updatedAt(LocalDateTime.now()).build();

        favoriteGroupRepository.save(favoriteGroup);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getFavoriteGroups(long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.EXAMPLE_USER_NOT_FOUND));

        // userId 값이 일치하는 데이터 조회
        List<FavoriteGroup> groupList = favoriteGroupRepository.findByUser(user);

        // GroupResponse 리스트로 변환
        List<GroupResponse> responseList = groupList.stream().map(group -> GroupResponse.builder().id(group.getId()).name(group.getName()).build()).toList();

        return responseList;
    }

    public void deleteGroup(long groupId) {
        FavoriteGroup group = favoriteGroupRepository.findById(groupId)
                .orElseThrow(() -> new ProfileException(ErrorCode.GROUP_NOT_FOUND));

        // 이미 삭제 처리 된 경우
        if (group.isDeleted()) {
            throw new ProfileException(ErrorCode.GROUP_ALREADY_DELETE);
        }

        group.setDeleted(true);

        favoriteGroupRepository.save(group);
    }

    public void editGroup(long groupId, GroupEditRequest request) {
        FavoriteGroup group = favoriteGroupRepository.findById(groupId)
                .orElseThrow(() -> new ProfileException(ErrorCode.GROUP_NOT_FOUND));

        boolean dupCheck = groupNameDuplicateCheck(request.getName(), group.getUser().getId());

        // 해당 유저가 이미 있는 그룹 이름을 입력했을 때
        if (dupCheck) {
            throw new ProfileException(ErrorCode.GROUP_ALREADY_EXIST);
        }
        group.setName(request.getName());

        favoriteGroupRepository.save(group);
    }


    // 입력한 그룹 이름이 이미 해당 userId가 가지고 있는지 확인
    public boolean groupNameDuplicateCheck(String groupName, long userId) {
        return favoriteGroupRepository.existsByUser_IdAndName(userId, groupName);
    }

    // 해당 그룹의 즐겨찾기 리스트를 조회
    public List<FavoriteResponse> getGroupItems(long groupId) {
        FavoriteGroup group = favoriteGroupRepository.findById(groupId)
                .orElseThrow(() -> new ProfileException(ErrorCode.GROUP_NOT_FOUND));

        List<Favorite> favoriteList = favoriteRepository.findAllByGroup(group);

        List<FavoriteResponse> favoriteResponseList = favoriteList.stream().map(favorite ->
                FavoriteResponse.builder()
                        .id(favorite.getId())
                        .userId(favorite.getUser().getId())
                        .groupId(favorite.getGroup().getId())
                        .placeId(favorite.getPlace().getId())
                        .memo(favorite.getMemo())
                        .createdAt(favorite.getCreatedAt())
                        .updatedAt(favorite.getUpdatedAt())
                        .isDeleted(favorite.isDeleted())
                        .lat(favorite.getPlace().getLat())
                        .lng(favorite.getPlace().getLng())
                        .address(favorite.getPlace().getAddress())
                        .build()
        ).toList();

        return favoriteResponseList;
    }

    // 해당 즐겨찾기의 메모를 수정
    public void editFavoriteMemo(long favoriteId, FavoriteEditRequest request) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new ProfileException(ErrorCode.FAVORITE_NOT_FOUND));

        favorite.setMemo(request.getMemo());

        favoriteRepository.save(favorite);
    }

    // 해당 즐겨찾기를 soft 삭제
    public void deleteFavorite(long favoriteId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new ProfileException(ErrorCode.FAVORITE_NOT_FOUND));

        long placeId = favorite.getPlace().getId();

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ProfileException(ErrorCode.PLACE_NOT_FOUND));

        // 즐겨찾기 개수 1 감소
        place.setFavoriteCount(place.getFavoriteCount() - 1);

        favorite.setDeleted(true);

        favoriteRepository.save(favorite);
        placeRepository.save(place);
    }

    // 그룹에 즐겨찾기를 추가
    public FavoriteAddResponse addFavorite(long groupId, FavoriteAddRequest request) {

        FavoriteGroup group = favoriteGroupRepository.findById(groupId)
                .orElseThrow(() -> new ProfileException(ErrorCode.GROUP_NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserException(ErrorCode.EXAMPLE_USER_NOT_FOUND));

        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new MapException(ErrorCode.PLACE_NOT_FOUND));

        Favorite favorite = Favorite.builder()
                .user(user)
                .group(group)
                .place(place)
                .memo(request.getMemo())
                .isDeleted(false)
                .build();

        // 즐겨찾기 개수 1 증가
        place.setFavoriteCount(place.getFavoriteCount() + 1);

        favoriteRepository.save(favorite);
        placeRepository.save(place);

        FavoriteAddResponse favoriteAddResponse = FavoriteAddResponse.builder()
                .placeId(favorite.getPlace().getId())
                .memo(favorite.getMemo())
                .lat(favorite.getPlace().getLat())
                .lng(favorite.getPlace().getLng())
                .address(favorite.getPlace().getAddress())
                .build();

        return favoriteAddResponse;
    }
}
