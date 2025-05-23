package com.even.zaro.controller;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.dto.post.*;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.jwt.JwtUtil;
import com.even.zaro.service.PostLikeService;
import com.even.zaro.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "게시판", description = "게시판 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.",security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(
            @RequestBody @Valid PostCreateRequest request,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
            ) {
        PostDetailResponse response = postService.createPost(request, userInfoDto.getUserId());
        return ResponseEntity.ok(
                ApiResponse.success("게시글이 작성 되었습니다.", response)
        );
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.",security = {@SecurityRequirement(name = "bearer-key")})
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostUpdateRequest request,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
    ) {
        postService.updatePost(postId, request, userInfoDto.getUserId());
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다."));
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 리스트 목록을 조회합니다.")
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "페이지 크기", in = ParameterIn.QUERY),
            @Parameter(name = "sort", description = "정렬 기준 (예: createdAt,desc)", in = ParameterIn.QUERY)
    })
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPostList(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
    ){
        Page<PostPreviewDto> posts = postService.getPostListPage(category, pageable);

        return ResponseEntity.ok(ApiResponse.success("게시글 리스트 조회가 성공했습니다.", Map.of(
                "content", posts.getContent(),
                "totalPages", posts.getTotalPages(),
                "currentPage", posts.getNumber() + 1,
                "totalElements", posts.getTotalElements()
        )));
    }



    @Operation(summary = "게시글 상세 조회", description = "게시글의 상세 내용을 조회합니다.",security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        PostDetailResponse response = postService.getPostDetail(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 상세 조회가 성공 했습니다.", response));
    }


    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.",security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
    ) {
        postService.deletePost(postId, userInfoDto.getUserId());
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }


    @Operation(summary = "홈 전용 게시글 미리보기 조회", description = "홈 화면에서 보여줄 게시글을 조회합니다.")
    @GetMapping("/home")
    public ResponseEntity<ApiResponse<HomePostPreviewResponse>> getHomePost(){
        HomePostPreviewResponse homePost = postService.getHomePostPreview();
        return ResponseEntity.ok(ApiResponse.success("홈 게시글 조회가 성공했습니다.", homePost));
    }

    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 누릅니다.",security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        postLikeService.likePost(postId, userInfoDto.getUserId());
        return ResponseEntity.ok(ApiResponse.success("해당 게시글 좋아요를 성공했습니다."));
    }

    @Operation(summary = "게시글 좋아요 취소", description = "게시글 좋아요를 취소합니다.",security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        postLikeService.unlikePost(postId, userInfoDto.getUserId());
        return ResponseEntity.ok(ApiResponse.success("해당 게시글 좋아요 취소를 성공했습니다."));
    }

}
