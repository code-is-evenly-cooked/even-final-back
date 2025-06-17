package com.even.zaro.service;

import com.even.zaro.entity.Post;
import com.even.zaro.entity.PostLike;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.post.PostException;
import com.even.zaro.repository.PostLikeRepository;
import com.even.zaro.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostService postService;

    @Transactional
    public void likePost(Long postId, Long userId) {
        if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new PostException(ErrorCode.ALREADY_LIKED);
        }

        User user = userService.findUserById(userId);
        userService.validateActiveUser(user);

        Post post = postService.findUndeletedPostOrThrow(postId);

        postLikeRepository.save(PostLike.builder()
                .user(user)
                .post(post)
                .build());
        post.changeLikeCount(post.getLikeCount() + 1);
        postService.updatePostScore(post);
    }


    @Transactional
    public void unlikePost(Long postId, Long userId) {
        User user = userService.findUserById(userId);
        userService.validateActiveUser(user);
        Post post = postService.findUndeletedPostOrThrow(postId);

        PostLike postLike = postLikeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new PostException(ErrorCode.LIKE_NOT_POST));

        postLikeRepository.delete(postLike);

        post.changeLikeCount(Math.max(0,post.getLikeCount() - 1));
        postService.updatePostScore(post);
    }

    @Transactional(readOnly = true)
    public boolean hasLikedPost(Long userId, Long postId) {
        postService.findUndeletedPostOrThrow(postId);
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }
}
