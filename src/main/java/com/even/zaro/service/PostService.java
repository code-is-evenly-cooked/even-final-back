package com.even.zaro.service;

import com.even.zaro.dto.post.*;
import com.even.zaro.entity.Post;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.post.PostException;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createPost(PostCreateRequest request, Long userId) {

        Post.Category category;

        try {
            category = Post.Category.valueOf(request.getCategory());
        } catch (IllegalArgumentException e){
            throw new PostException(ErrorCode.INVALID_CATEGORY);
        }

        if (category == Post.Category.RANDOM_BUY &&
                (request.getImageUrlList() == null || request.getImageUrlList().isEmpty())) {
            throw new PostException(ErrorCode.IMAGE_REQUIRED_FOR_RANDOM_BUY);
        }

        Post.Tag tag = convertTag(request.getTag());

        if (!category.isAllowed(tag)){
            throw new PostException(ErrorCode.INVALID_TAG_FOR_CATEGORY);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PostException(ErrorCode.USER_NOT_FOUND));

        String thumbnailUrl = request.getThumbnailUrl();
        List<String> imageUrls = request.getImageUrlList();

        if (thumbnailUrl != null) {
            if (imageUrls == null || !imageUrls.contains(thumbnailUrl)) {
                throw new PostException(ErrorCode.THUMBNAIL_NOT_IN_IMAGE_LIST);
            }
        }
        if (thumbnailUrl == null && imageUrls != null && !imageUrls.isEmpty()) {
            thumbnailUrl = imageUrls.get(0);
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(category)
                .tag(tag)
                .thumbnailUrl(thumbnailUrl)
                .user(user)
                .build();

        postRepository.save(post);

        return post.getId();
    }

    private Post.Tag convertTag(String tag) {
        try {
            return Post.Tag.valueOf(tag);
        } catch (IllegalArgumentException e) {
            throw new PostException(ErrorCode.INVALID_TAG);
        }
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new PostException(ErrorCode.INVALID_POST_OWNER);
        }

        if (post.getCategory() == Post.Category.RANDOM_BUY &&
                (request.getImageUrlList() == null || request.getImageUrlList().isEmpty())) {
            throw new PostException(ErrorCode.IMAGE_REQUIRED_FOR_RANDOM_BUY);
        }

        Post.Tag tag = convertTag(request.getTag());

        Post.Category category = post.getCategory();
        if (!category.isAllowed(tag)){
            throw new PostException(ErrorCode.INVALID_TAG_FOR_CATEGORY);
        }

        List<String> imageUrls = request.getImageUrlList();
        String thumbnail = request.getThumbnailUrl();
        if (thumbnail != null) {
            if (imageUrls == null || !imageUrls.contains(thumbnail)) {
                throw new PostException(ErrorCode.THUMBNAIL_NOT_IN_IMAGE_LIST);
            }
        }

        if (thumbnail == null && imageUrls != null && !imageUrls.isEmpty()) {
            thumbnail = imageUrls.get(0);
        }


        post.update(
                request.getTitle(),
                request.getContent(),
                tag,
                imageUrls,
                thumbnail
        );
    }

    @Transactional
    public PostListResponse getPostList(String category, Pageable pageable) {
        Page<Post> page;

        if(category == null || category.isBlank()) {
            page = postRepository.findByIsDeletedFalse(pageable);
        } else {
            Post.Category postCategory;
            try {
                postCategory = Post.Category.valueOf(category);
            } catch (IllegalArgumentException e){
                throw new PostException(ErrorCode.INVALID_CATEGORY);
            }
            page = postRepository.findByCategoryAndIsDeletedFalse(postCategory,pageable);
        }
        List<PostPreviewDto> posts = page.getContent().stream()
                .map(PostPreviewDto::from)
                .toList();

        PostListResponse.PageInfo pageInfo = PostListResponse.PageInfo.builder()
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .build();

        return PostListResponse.builder()
                .posts(posts)
                .pageInfo(pageInfo)
                .build();
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        User user = post.getUser();

        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .category(String.valueOf(post.getCategory()))
                .tag(String.valueOf(post.getTag()))
                .thumbnailUrl(post.getThumbnailUrl())
                .imageUrlList(post.getImageUrlList())
                .user(new PostDetailResponse.UserInfo(
                        user.getId(),
                        user.getNickname(),
                        user.getProfileImage()))
        .build();
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new PostException(ErrorCode.INVALID_POST_OWNER);
        }
        post.softDelete();
    }


    // 공통 로직 분리
    private Post.Category parseCategory(String category) {
        try {
            return Post.Category.valueOf(category);
        } catch (IllegalArgumentException e){
            throw new PostException(ErrorCode.INVALID_CATEGORY);
        }
    }


    private Post.Tag convertTage(String tag) {
        try {
            return Post.Tag.valueOf(tag);
        } catch (IllegalArgumentException e){
            throw new PostException(ErrorCode.INVALID_TAG);
        }
    }


    private void validateImageRequirement(Post.Category category, List<String> imageUrls) {
        if (category == Post.Category.RANDOM_BUY &&
                (imageUrls == null || imageUrls.isEmpty())) {
            throw new PostException(ErrorCode.IMAGE_REQUIRED_FOR_RANDOM_BUY);
        }
    }


    private void validateTagForCategory(Post.Category category, Post.Tag tag) {
        if (!category.isAllowed(tag)){
            throw new PostException(ErrorCode.INVALID_TAG_FOR_CATEGORY);
        }
    }


    private String resolveThumbnail(String thumbnailUrl, List<String> imageUrls) {
        if (thumbnailUrl != null) {
            if (imageUrls == null || !imageUrls.contains(thumbnailUrl)) {
                throw new PostException(ErrorCode.THUMBNAIL_NOT_IN_IMAGE_LIST);
            }
            return thumbnailUrl;
        }

        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }

        return null;
    }
}
