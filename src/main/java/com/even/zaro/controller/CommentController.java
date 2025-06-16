package com.even.zaro.controller;

import com.even.zaro.dto.PageResponse;
import com.even.zaro.dto.comment.CommentRequestDto;
import com.even.zaro.dto.comment.CommentResponseDto;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.dto.post.ReportRequestDTO;
import com.even.zaro.dto.post.ReportResponseDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.CommentReportService;
import com.even.zaro.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final CommentReportService commentReportService;

    @Operation(summary = "댓글 생성(또는 답글 멘션)",
            description = """
                {postId} 게시글에 댓글을 작성합니다.
        
                - 일반 댓글 또는 답글(멘션)을 작성할 수 있습니다.
                - 답글의 경우, `taggedNickname` 필드에 멘션할 사용자의 닉네임을 포함해 주세요.
                - 멘션을 지우거나 일반 댓글인 경우, `taggedNickname`을 null로 보내거나 생략해도 됩니다.
                """,
            security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
            @RequestParam(defaultValue = "10") int pageSize,
            @Valid @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        CommentResponseDto responseDto = commentService.createComment(postId, requestDto, userInfoDto, pageSize);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글을 작성했습니다.", responseDto));
    }

    @Operation(summary = "댓글 리스트 조회", description = "{postId} 게시글에 댓글 리스트를 조회합니다.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentPageResponse>> readAllComments(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
    ) {
        CommentPageResponse responseDto = commentService.readAllComments(postId, pageable, userInfoDto);
        return ResponseEntity.ok(ApiResponse.success("댓글 리스트를 불러왔습니다.", responseDto));
    }

    @Operation(summary = "댓글 수정", description = "{commentId}에 해당하는 댓글 내용을 수정합니다.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @PatchMapping("comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @Parameter(description = "댓글 ID", example = "1") @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
    ) {
        CommentResponseDto responseDto = commentService.updateComment(commentId, requestDto, userInfoDto);
        return ResponseEntity.ok(ApiResponse.success("댓글을 수정했습니다.", responseDto));
    }

    @Operation(summary = "댓글 삭제", description = "{commentId}에 해당하는 댓글을 삭제합니다.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteComment(
            @Parameter(description = "댓글 ID", example = "1") @PathVariable Long commentId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        commentService.softDeleteComment(commentId, userInfoDto);
        return ResponseEntity.ok(ApiResponse.success("댓글을 삭제했습니다."));
    }

    @Operation(summary = "댓글 신고", description = "{commentId} 댓글을 신고합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping("comments/{commentId}/report")
    public ResponseEntity<ApiResponse<ReportResponseDto>> reportComment(
            @Parameter(description = "댓글 ID", example = "1") @PathVariable Long commentId,
            @RequestBody ReportRequestDTO requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
            ){
        ReportResponseDto responseDto = commentReportService.reportComment(commentId, requestDto, userInfoDto.getUserId());
        return ResponseEntity.ok(ApiResponse.success("댓글을 신고했습니다.", responseDto));
    }


}
