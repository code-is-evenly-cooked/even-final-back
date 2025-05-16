package com.even.zaro.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCommentDto {

    @Schema(description = "게시글 ID", example = "1189")
    private Long postId;

    @Schema(description = "게시글 제목", example = "자취 꿀팁 모음")
    private String title;

    @Schema(description = "게시글 카테고리", example = "DAILY_LIFE")
    private String category;

    @Schema(description = "게시글 태그", example = "TIPS")
    private String tag;

    @Schema(description = "게시글 좋아요 수", example = "77")
    private int likeCount;

    @Schema(description = "게시글 댓글 수", example = "3")
    private int commentCount;

    // 댓글 정보
    @Schema(description = "댓글 내용", example = "와 대박 정말진짜 유용한 정보네요~!")
    private String commentContent;

    @Schema(description = "댓글 작성일시", example = "2025-05-09T13:00:00")
    private LocalDateTime commentCreatedAt;
}
