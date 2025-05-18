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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createPost(PostCreateRequest request, Long userId) {

        Post.Category category = parseCategory(request.getCategory());
        validateImageRequirement(category, request.getImageUrlList());

        Post.Tag tag = convertTag(request.getTag());
        validateTagForCategory(category, tag);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PostException(ErrorCode.USER_NOT_FOUND));

        if (!user.isVerified()) {
            throw new PostException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        String thumbnailUrl = resolveThumbnail(request.getThumbnailUrl(), request.getImageUrlList());

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(category)
                .tag(tag)
                .thumbnailUrl(thumbnailUrl)
                .imageUrlList(request.getImageUrlList())
                .user(user)
                .build();

        postRepository.save(post);
        return post.getId();
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new PostException(ErrorCode.INVALID_POST_OWNER);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PostException(ErrorCode.USER_NOT_FOUND));

        if (!user.isVerified()) {
            throw new PostException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        validateImageRequirement(post.getCategory(), request.getImageUrlList());

        Post.Tag tag = convertTag(request.getTag());

        validateTagForCategory(post.getCategory(), tag);

        String thumbnailUrl = resolveThumbnail(request.getThumbnailUrl(), request.getImageUrlList());


        post.changeTitle(request.getTitle());
        post.changeContent(request.getContent());
        post.changeTag(tag);
        post.changeImageUrlList(request.getImageUrlList());
        post.changeThumbnailUrl(thumbnailUrl);
    }

    @Transactional(readOnly = true)
    public Page<PostPreviewDto> getPostListPage(String category, Pageable pageable) {
        Page<Post> page;

        if (category == null || category.isBlank()) {
            page = postRepository.findByIsDeletedFalse(pageable);
        } else {
            Post.Category postCategory = parseCategory(category);
            page = postRepository.findByCategoryAndIsDeletedFalse(postCategory, pageable);
        }

        return page.map(PostPreviewDto::from);
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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PostException(ErrorCode.USER_NOT_FOUND));

        if (!user.isVerified()) {
            throw new PostException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        post.markAsDeleted();
    }


    @Transactional
    public HomePostPreviewResponse getHomePostPreview() {
        List<Post> togetherPosts = postRepository.findTop5ByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(Post.Category.TOGETHER);
        List<Post> dailyLifePosts = postRepository.findTop5ByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(Post.Category.DAILY_LIFE);
        List<Post> randomBuyPosts = postRepository.findTop5ByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(Post.Category.RANDOM_BUY);

        List<HomePostPreviewResponse.SimplePostDto> together = togetherPosts.stream()
                .map(post -> HomePostPreviewResponse.SimplePostDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .createAt(formatDate(post.getCreatedAt()))
                        .build())
                .toList();

        List<HomePostPreviewResponse.SimplePostDto> dailyLife = dailyLifePosts.stream()
                .map(post -> HomePostPreviewResponse.SimplePostDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .createAt(formatDate(post.getCreatedAt()))
                        .build())
                .toList();

        List<HomePostPreviewResponse.RandomBuyPostDto> randomBuy = randomBuyPosts.stream()
                .map(post -> HomePostPreviewResponse.RandomBuyPostDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .content(generatePreview(post.getContent()))
                        .thumbnailUrl(post.getThumbnailUrl())
                        .likeCount(post.getLikeCount())
                        .commentCount(post.getCommentCount())
                        .writerProfileImage(post.getUser().getProfileImage())
                        .writerNickname(post.getUser().getNickname())
                        .createAt(formatDate(post.getCreatedAt()))
                        .build())
                .toList();

        return HomePostPreviewResponse.builder()
                .together(together)
                .dailyLife(dailyLife)
                .randomBuy(randomBuy)
                .build();
    }


    // 공통 로직 분리
    private Post.Category parseCategory(String category) {
        try {
            return Post.Category.valueOf(category);
        } catch (IllegalArgumentException e){
            throw new PostException(ErrorCode.INVALID_CATEGORY);
        }
    }


    private Post.Tag convertTag(String tag) {
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

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.toLocalDate().toString();
    }
    private String generatePreview(String content) {
        return content.length() <= 30 ? content : content.substring(0, 30) + "...";
    }
}
