package com.even.zaro.service;

import com.even.zaro.entity.Post;
import com.even.zaro.entity.PostLike;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.post.PostException;
import com.even.zaro.repository.PostLikeRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void likePost(Long postId, Long userId) {
        if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new PostException(ErrorCode.ALREADY_LIKED);
        }

        User user = validateActiveUser(userId);
        Post post = validatePost(postId);

        postLikeRepository.save(PostLike.builder()
                .user(user)
                .post(post)
                .build());
        post.changeLikeCount(post.getLikeCount() + 1);
    }


    @Transactional
    public void unlikePost(Long postId, Long userId) {
        validateActiveUser(userId);
        Post post = validatePost(postId);

        PostLike postLike = postLikeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new PostException(ErrorCode.LIKE_NOT_POST));

        postLikeRepository.delete(postLike);

        post.changeLikeCount(Math.max(0,post.getLikeCount() - 1));
    }

    // 공통로직
    private User validateActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PostException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != Status.ACTIVE) {
            throw new PostException(ErrorCode.EMAIL_NOT_VERIFIED_LIKE);
        }

        return user;
    }

    private Post validatePost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    }
}
