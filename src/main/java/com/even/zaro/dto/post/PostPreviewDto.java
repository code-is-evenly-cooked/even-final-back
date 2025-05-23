package com.even.zaro.dto.post;

import com.even.zaro.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "게시글 목록 응답 DTO")
public class PostPreviewDto {
    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 제목", example = "오늘의 자취 꿀템 공유")
    private String title;

    @Schema(description = "미리보기용 게시글 내용", example = "이건 제가 진짜 매일 쓰는 꿀템인데요...")
    private String content;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/images/thumb1.jpg")
    private String thumbnailUrl;

    @Schema(description = "게시글 카테고리", example = "DAILY_LIFE")
    private String category;

    @Schema(description = "게시글 태그", example = "TIP")
    private String tag;

    @Schema(description = "좋아요 수", example = "5")
    private int likeCount;

    @Schema(description = "댓글 수", example = "3")
    private int commentCount;

    @Schema(description = "게시글 생성 일시", example = "2025-05-21T12:34:56")
    private LocalDateTime createdAt;

    public static PostPreviewDto from(Post post) {
        return PostPreviewDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(truncate(post.getContent(), 50))
                .thumbnailUrl(post.getThumbnailUrl())
                .category(post.getCategory().name())
                .tag(post.getTag() != null ? post.getTag().name() : null)
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private static String truncate(String content, int maxLength) {
        if (content == null) return "";
        return content.length() <= maxLength ? content : content.substring(0, maxLength) + "...";
    }
}
