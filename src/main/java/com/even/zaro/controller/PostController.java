package com.even.zaro.controller;

import com.even.zaro.dto.post.*;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import com.even.zaro.jwt.JwtUtil;
import com.even.zaro.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시판", description = "게시판 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @RequestBody @Valid PostCreateRequest request,
            HttpServletRequest servletRequest
    ) {
        Long userId = getAuthenticatedUserId(servletRequest);
        Long postId = postService.createPost(request, userId);
        return ResponseEntity.ok(
                ApiResponse.success("게시글이 작성 되었습니다.",postId)
        );
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequest request,
            HttpServletRequest servletRequest
    ) {
        Long userId = getAuthenticatedUserId(servletRequest);
        postService.updatePost(postId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다.", null));
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 리스트 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PostListResponse>> getPostList(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10)Pageable pageable
            )
    {
        PostListResponse response = postService.getPostList(category, pageable);
        return ResponseEntity.ok(ApiResponse.success("게시글 리스트 조회가 성공 했습니다.", response));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글의 상세 내용을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(@PathVariable Long postId) {
        PostDetailResponse response = postService.getPostDetail(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 상세 조회가 성공 했습니다.", response));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            HttpServletRequest servletRequest
    ) {
        Long userId = getAuthenticatedUserId(servletRequest);
        postService.deletePost(postId, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다.", null));
    }

    private Long getAuthenticatedUserId(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        if (token == null || !jwtUtil.validateAccessToken(token)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        return Long.valueOf(jwtUtil.getUserIdFromToken(token));
    }

}
