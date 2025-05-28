package com.even.zaro.service;

import com.even.zaro.dto.PageResponse;
import com.even.zaro.dto.profile.*;
import com.even.zaro.repository.*;
import com.even.zaro.entity.*;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.comment.CommentException;
import com.even.zaro.global.exception.profile.ProfileException;
import com.even.zaro.global.exception.user.UserException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class ProfileService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;

    // 유저 기본 프로필 조회
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

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
    public PageResponse<UserPostDto> getUserPosts(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Page<UserPostDto> page = postRepository.findByUserAndIsDeletedFalse(user, pageable)
                .map(post -> UserPostDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .category(post.getCategory().name())
                        .tag(post.getTag() != null ? post.getTag().name() : null)
                        .thumbnailImage(post.getThumbnailImage())
                        .likeCount(post.getLikeCount())
                        .commentCount(post.getCommentCount())
                        .createdAt(post.getCreatedAt())
                        .build());

        return new PageResponse<>(page);
    }

    // 유저가 좋아요 누른 게시물 list 조회
    public Page<UserPostDto> getUserLikedPosts(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return postLikeRepository.findByUser(user, pageable)
                .map(postLike -> UserPostDto.builder()
                        .postId(postLike.getPost().getId())
                        .title(postLike.getPost().getTitle())
                        .content(postLike.getPost().getContent())
                        .category(postLike.getPost().getCategory().name())
                        .tag(postLike.getPost().getTag() != null ? postLike.getPost().getTag().name() : null)
                        .thumbnailImage(postLike.getPost().getThumbnailImage())
                        .likeCount(postLike.getPost().getLikeCount())
                        .commentCount(postLike.getPost().getCommentCount())
                        .createdAt(postLike.getPost().getCreatedAt())
                        .build());
    }

    // 유저가 작성한 댓글 list 조회
    public Page<UserCommentDto> getUserComments(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return commentRepository.findByUserAndIsDeletedFalse(user, pageable)
                .map(comment -> {
                    Post post = comment.getPost();
                    if (post == null) {
                        throw new CommentException(ErrorCode.COMMENT_NO_ASSOCIATED_POST);
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
            throw new ProfileException(ErrorCode.FOLLOW_SELF_NOT_ALLOWED);
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        // 이미 팔로우되어 있는지 확인
        boolean alreadyFollowing = followRepository.existsByFollowerAndFollowee(follower, followee);
        if (alreadyFollowing) {
            throw new ProfileException(ErrorCode.FOLLOW_ALREADY_EXISTS);
        }

        // 팔로우 저장
        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();
        followRepository.save(follow);

        // 팔로잉 & 팔로워 카운트 증가
        follower.setFollowingCount(follower.getFollowingCount() + 1);
        followee.setFollowerCount(followee.getFollowerCount() + 1);

        userRepository.save(follower);
        userRepository.save(followee);
    }

    // 다른 유저 언팔로우 하기
    public void unfollowUser(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new ProfileException(ErrorCode.FOLLOW_UNFOLLOW_SELF_NOT_ALLOWED);
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee)
                .orElseThrow(() -> new ProfileException(ErrorCode.FOLLOW_NOT_EXIST));

        followRepository.delete(follow);

        // 팔로잉 & 팔로워 카운트 감소
        follower.setFollowingCount(follower.getFollowingCount() - 1);
        followee.setFollowerCount(followee.getFollowerCount() - 1);

        userRepository.save(follower);
        userRepository.save(followee);
    }

    // 팔로잉 목록 조회
    public List<FollowerFollowingListDto> getUserFollowings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return followRepository.findByFollower(user).stream()
                .map(follow -> FollowerFollowingListDto.builder()
                        .userId(follow.getFollowee().getId())
                        .userName(follow.getFollowee().getNickname())
                        .profileImage(follow.getFollowee().getProfileImage())
                        .build())
                .toList();
    }

    // 팔로워 목록 조회
    public List<FollowerFollowingListDto> getUserFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return followRepository.findByFollowee(user).stream()
                .map(follow -> FollowerFollowingListDto.builder()
                        .userId(follow.getFollower().getId())
                        .userName(follow.getFollower().getNickname())
                        .profileImage(follow.getFollower().getProfileImage())
                        .build())
                .toList();
    }
}
