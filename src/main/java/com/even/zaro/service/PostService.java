package com.even.zaro.service;

import com.even.zaro.dto.PageResponse;
import com.even.zaro.dto.post.*;
import com.even.zaro.entity.Post;
import com.even.zaro.entity.User;
import com.even.zaro.event.PostDeletedEvent;
import com.even.zaro.event.PostSavedEvent;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.post.PostException;
import com.even.zaro.mapper.PostMapper;
import com.even.zaro.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;
    private final PostMapper postMapper;

    @Transactional
    public PostDetailResponse createPost(PostCreateRequest request, Long userId) {

        Post.Category category = parseCategory(request.getCategory());
        validateImageRequirement(category, request.getPostImageList());

        Post.Tag tag = convertTag(request.getTag());
        validateTagForCategory(category, tag);

        User user = userService.findUserById(userId);
        userService.validateNotPending(user);

        String thumbnailImage = resolveThumbnail(request.getThumbnailImage(), request.getPostImageList());

        Post post = Post.create(
                request.getTitle(),
                request.getContent(),
                category,
                tag,
                thumbnailImage,
                request.getPostImageList(),
                user
        );

        Post saved = postRepository.save(post);
        eventPublisher.publishEvent(new PostSavedEvent(saved));

        return postMapper.toPostDetailDto(saved);
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = findPostOrThrow(postId);

        User user = userService.findUserById(userId);
        userService.validateNotPending(user);

        validatePostNotOwner(post, user);

        validateImageRequirement(post.getCategory(), request.getPostImageList());
        Post.Tag tag = convertTag(request.getTag());
        validateTagForCategory(post.getCategory(), tag);

        String thumbnailImage = resolveThumbnail(request.getThumbnailImage(), request.getPostImageList());

        post.update(
                request.getTitle(),
                request.getContent(),
                tag,
                request.getPostImageList(),
                thumbnailImage
        );
        eventPublisher.publishEvent(new PostSavedEvent(post));
    }

    @Transactional(readOnly = true)
    public PageResponse<PostPreviewDto> getPostListPage(String category, String tag, Pageable pageable) {
        Page<Post> page = resolvePostList(category, tag, pageable);
        return new PageResponse<>(page.map(postMapper::toPostPreviewDto));
    }

    private Page<Post> resolvePostList(String category, String tag, Pageable pageable) {

        boolean categoryEmpty = isBlank(category);
        boolean tagEmpty = isBlank(tag);

        if (categoryEmpty && tagEmpty) {
            return postRepository.findByIsDeletedFalseAndIsReportedFalse(pageable);
        }
        if (!categoryEmpty && tagEmpty) {
            Post.Category postCategory = parseCategory(category);
            return postRepository.findByCategoryAndIsDeletedFalseAndIsReportedFalse(postCategory, pageable);
        }
        if (!categoryEmpty) {
            Post.Category postCategory = parseCategory(category);
            Post.Tag postTag = convertTag(tag);
            validateTagForCategory(postCategory, postTag);
            return postRepository.findByCategoryAndTagAndIsDeletedFalseAndIsReportedFalse(postCategory, postTag, pageable);
        }
            throw new PostException(ErrorCode.INVALID_CATEGORY);
        }
    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }


    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = findPostOrThrow(postId);
        return postMapper.toPostDetailDto(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = findPostOrThrow(postId);

        User user = userService.findUserById(userId);
        userService.validateNotPending(user);

        validatePostNotOwner(post, user);

        post.markAsDeleted();
        eventPublisher.publishEvent(new PostDeletedEvent(postId));
    }


    @Transactional
    public HomePostPreviewResponse getHomePostPreview() {
        List<Post> togetherPosts = postRepository.findTop5ByCategoryAndIsDeletedFalseAndIsReportedFalseOrderByCreatedAtDesc(Post.Category.TOGETHER);
        List<Post> dailyLifePosts = postRepository.findTop5ByCategoryAndIsDeletedFalseAndIsReportedFalseOrderByCreatedAtDesc(Post.Category.DAILY_LIFE);
        List<Post> randomBuyPosts = postRepository.findTop5ByCategoryAndIsDeletedFalseAndIsReportedFalseOrderByCreatedAtDesc(Post.Category.RANDOM_BUY);

        List<HomePostPreviewResponse.SimplePostDto> together = togetherPosts.stream()
                .map(postMapper::toSimplePostDto)
                .toList();

        List<HomePostPreviewResponse.SimplePostDto> dailyLife = dailyLifePosts.stream()
                .map(postMapper::toSimplePostDto)
                .toList();

        List<HomePostPreviewResponse.RandomBuyPostDto> randomBuy = randomBuyPosts.stream()
                .map(postMapper::toRandomBuyDto)
                .toList();

        return HomePostPreviewResponse.builder()
                .together(together)
                .dailyLife(dailyLife)
                .randomBuy(randomBuy)
                .build();
    }

    @Transactional
    public List<PostRankResponseDto> getRankedPosts() {
        List<Post> posts = postRepository.findTopPosts(0, PageRequest.of(0, 5));

        return posts.stream()
                .map(postMapper::toRankDto)
                .toList();
    }

    @Transactional
    public void updatePostScore(Post post) {
        post.updateScore();
    }

    // 공통 로직 분리
    /// 게시글을 찾을 수 없을때
    public Post findPostOrThrow(Long postId) {
        Post post =  postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        if (post.isDeleted() || post.isReported()) {
            throw new PostException(ErrorCode.POST_NOT_FOUND);
        }

        return post;
    }

    /// 게시글 찾을 없을때 - 삭제만 검사 (postLike)
    public Post findUndeletedPostOrThrow(Long postId) {
        return postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    }


    /// 올바르지 않은 카테고리 일때
    private Post.Category parseCategory(String category) {
        try {
            return Post.Category.valueOf(category);
        } catch (IllegalArgumentException e){
            throw new PostException(ErrorCode.INVALID_CATEGORY);
        }
    }


    /// 올바르지 않은 태그 일때
    private Post.Tag convertTag(String tag) {
        try {
            return Post.Tag.valueOf(tag);
        } catch (IllegalArgumentException e){
            throw new PostException(ErrorCode.INVALID_TAG);
        }
    }


    /// 텅장일기 카테고리 - 이미지 없을때
    private void validateImageRequirement(Post.Category category, List<String> postImages) {
        if (category == Post.Category.RANDOM_BUY &&
                (postImages == null || postImages.isEmpty())) {
            throw new PostException(ErrorCode.IMAGE_REQUIRED_FOR_RANDOM_BUY);
        }
    }

    /// 카테고리랑 일치하지 않은 태그를 선택했을때
    private void validateTagForCategory(Post.Category category, Post.Tag tag) {
        if (!category.isAllowed(tag)){
            throw new PostException(ErrorCode.INVALID_TAG_FOR_CATEGORY);
        }
    }


    /// 이미지 리스트에 없는 썸네일 이미지를 넣었을때
    private String resolveThumbnail(String thumbnailImage, List<String> postImages) {
        if (thumbnailImage != null) {
            if (postImages == null || !postImages.contains(thumbnailImage)) {
                throw new PostException(ErrorCode.THUMBNAIL_NOT_IN_IMAGE_LIST);
            }
            return thumbnailImage;
        }

        if (postImages != null && !postImages.isEmpty()) {
            return postImages.get(0);
        }

        return null;
    }

    /// 수정, 삭제 권한이 없을때
    private void validatePostNotOwner(Post post, User user) {
        if (!post.getUser().equals(user)) {
            throw new PostException(ErrorCode.INVALID_POST_OWNER);
        }
    }
}
