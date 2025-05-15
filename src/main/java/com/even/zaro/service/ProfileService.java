package com.even.zaro.service;

import com.even.zaro.dto.UserPostDto;
import com.even.zaro.dto.UserProfileDto;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.repository.PostLikeRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final UserRepository userRepository;
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
}
