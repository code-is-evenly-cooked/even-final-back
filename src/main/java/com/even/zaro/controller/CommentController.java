package com.even.zaro.controller;

import com.even.zaro.dto.comment.CommentRequestDto;
import com.even.zaro.dto.comment.CommentResponseDto;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        CommentResponseDto responseDto = commentService.createComment(postId, requestDto, userInfoDto);
        return ResponseEntity.ok(ApiResponse.success("댓글을 작성했습니다.", responseDto));
    }
}
