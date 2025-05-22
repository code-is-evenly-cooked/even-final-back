package com.even.zaro.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "댓글 응답 dto")
public class CommentResponseDto {

    @Schema(description = "댓글 ID", example = "1")
    private Long id;

    @Schema(description = "댓글 내용", example = "너무 좋네요!!")
    private String content;

    @Schema(description = "작성자 닉네임", example = "이브니")
    private String nickname;

    @Schema(description = "작성자 프로필 이미지", example = "https://.../profile.png")
    private String profileImage;

    @Schema(description = "자취 시작일", example = "2024-03-01")
    private LocalDate liveAloneDate;

    @Schema(description = "댓글 작성 시간", example = "2025-05-21T14:33:00")
    private LocalDateTime createdAt;
}
