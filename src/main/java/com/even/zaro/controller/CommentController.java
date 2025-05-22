package com.even.zaro.controller;

import com.even.zaro.dto.PageResponse;
import com.even.zaro.dto.comment.CommentRequestDto;
import com.even.zaro.dto.comment.CommentResponseDto;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 생성", description = "{postId} 게시글에 댓글을 작성합니다.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        CommentResponseDto responseDto = commentService.createComment(postId, requestDto, userInfoDto);
        return ResponseEntity.ok(ApiResponse.success("댓글을 작성했습니다.", responseDto));
    }

    @Operation(summary = "댓글 리스트 조회", description = "{postId} 게시글에 댓글 리스트를 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponseDto>>> readAllComments(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
    ) {
        PageResponse<CommentResponseDto> responseDto = commentService.readAllComments(postId, pageable, userInfoDto);
        return ResponseEntity.ok(ApiResponse.success("댓글 리스트를 불러왔습니다.", responseDto));
    }

    @Operation(summary = "댓글 수정", description = "{commentId}에 해당하는 댓글 내용을 수정합니다.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @PatchMapping("comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @Parameter(description = "댓글 ID", example = "1") @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
    ) {
        CommentResponseDto responseDto = commentService.updateComment(commentId, requestDto, userInfoDto);
        return ResponseEntity.ok(ApiResponse.success("댓글을 수정했습니다.", responseDto));
    }
}
