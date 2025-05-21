package com.even.zaro.controller;

import com.even.zaro.dto.post.*;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import com.even.zaro.jwt.JwtUtil;
import com.even.zaro.service.PostLikeService;
import com.even.zaro.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;

import java.util.List;
import java.util.Map;

@Tag(name = "게시판", description = "게시판 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;
    private final PostLikeService postLikeService;

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(
            @RequestBody @Valid PostCreateRequest request,
            HttpServletRequest servletRequest
    ) {
        Long userId = getAuthenticatedUserId(servletRequest, ErrorCode.NEED_LOGIN_POST_CREATE);
        PostDetailResponse response = postService.createPost(request, userId);
        return ResponseEntity.ok(
                ApiResponse.success("게시글이 작성 되었습니다.", response)
        );
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequest request,
            HttpServletRequest servletRequest
    ) {
        Long userId = getAuthenticatedUserId(servletRequest, ErrorCode.NEED_LOGIN_POST_UPDATE);
        postService.updatePost(postId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다."));
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 리스트 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPostList(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10) Pageable pageable,
            HttpServletRequest request
    ){
        getAuthenticatedUserId(request, ErrorCode.NEED_LOGIN_POST);
        Page<PostPreviewDto> posts = postService.getPostListPage(category, pageable);

        return ResponseEntity.ok(ApiResponse.success("게시글 리스트 조회가 성공했습니다.", Map.of(
                "content", posts.getContent(),
                "totalPages", posts.getTotalPages(),
                "currentPage", posts.getNumber() + 1,
                "totalElements", posts.getTotalElements()
        )));
    }



    @Operation(summary = "게시글 상세 조회", description = "게시글의 상세 내용을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(
            @PathVariable Long postId,
            HttpServletRequest request) {
        getAuthenticatedUserId(request, ErrorCode.NEED_LOGIN_POST);
        PostDetailResponse response = postService.getPostDetail(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 상세 조회가 성공 했습니다.", response));
    }


    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            HttpServletRequest servletRequest
    ) {
        Long userId = getAuthenticatedUserId(servletRequest, ErrorCode.NEED_LOGIN_POST_DELETE);
        postService.deletePost(postId, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }


    @Operation(summary = "홈 전용 게시글 미리보기 조회", description = "홈 화면에서 보여줄 게시글을 조회합니다.")
    @GetMapping("/home")
    public ResponseEntity<ApiResponse<HomePostPreviewResponse>> getHomePost(){
        HomePostPreviewResponse homePost = postService.getHomePostPreview();
        return ResponseEntity.ok(ApiResponse.success("홈 게시글 조회가 성공했습니다.", homePost));
    }

    private Long getAuthenticatedUserId(HttpServletRequest request, ErrorCode errorCode) {
        String token = jwtUtil.resolveToken(request);
        if (token == null || !jwtUtil.validateAccessToken(token)) {
            throw new CustomException(errorCode);
        }
        return Long.valueOf(jwtUtil.getUserIdFromToken(token));
    }

    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 누릅니다.")
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long postId,
            HttpServletRequest request) {
        Long userId = getAuthenticatedUserId(request, ErrorCode.NEED_LOGIN_POST);
        postLikeService.likePost(postId, userId);
        return ResponseEntity.ok(ApiResponse.success("해당 게시글 좋아요를 성공했습니다."));
    }

    @Operation(summary = "게시글 좋아요 취소", description = "게시글 좋아요를 취소합니다.")
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> unlikePost(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = getAuthenticatedUserId(request, ErrorCode.NEED_LOGIN_POST);
        postLikeService.unlikePost(postId, userId);
        return ResponseEntity.ok(ApiResponse.success("해당 게시글 좋아요 취소를 성공했습니다."));
    }

    @Operation(summary = "게시글 좋아요 단일 조회", description = "현재 사용자가 해당 게시글에 좋아요를 눌렀는지 확인합니다.")
    @GetMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Boolean>> checkLiked(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = getAuthenticatedUserId(request, ErrorCode.NEED_LOGIN_POST);
        boolean liked = postLikeService.hasLiked(userId, postId);
        return ResponseEntity.ok(ApiResponse.success("해당 게시글 좋아요 여부 조회가 성공했습니다.", liked));
    }

    @Operation(summary = "좋아요한 게시글 전체 조회", description = "현재 사용자가 좋아요한 게시글 전체를 조회합니다.")
    @GetMapping("/like")
    public ResponseEntity<ApiResponse<List<PostLikeResponse>>> getLikedPosts(HttpServletRequest request) {
        Long userId = getAuthenticatedUserId(request, ErrorCode.NEED_LOGIN_POST);
        List<PostLikeResponse> likedPosts = postLikeService.getMyLikedPosts(userId);
        return ResponseEntity.ok(ApiResponse.success("좋아요한 게시글 조회가 성공했습니다.", likedPosts));
    }

}
