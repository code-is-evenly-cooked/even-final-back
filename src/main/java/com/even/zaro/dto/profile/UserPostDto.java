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
public class UserPostDto {

    @Schema(description = "게시글 ID", example = "999")
    private Long postId;

    @Schema(description = "게시글 제목", example = "같이 이거 살 파티원 모집급구.")
    private String title;

    @Schema(description = "게시글 내용", example = "이거시 얼마나 가성비물건이냐믄요 이러쿵 저러쿵 ~ 정말 짱이죠 ? 네 같이 사요")
    private String content;

    @Schema(description = "게시글 카테고리", example = "TOGETHER")
    private String category;

    @Schema(description = "게시글 태그", example = "BUY_TOGETHER")
    private String tag;

    @Schema(description = "게시글 이미지 URL", example = "/images/post/uuid.png")
    private String thumbnailImage;

    @Schema(description = "좋아요 수", example = "15")
    private int likeCount;

    @Schema(description = "댓글 수", example = "3")
    private int commentCount;

    @Schema(description = "게시글 생성일시", example = "2025-05-09T12:00:00")
    private LocalDateTime createdAt;
}
