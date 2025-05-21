package com.even.zaro.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "게시글 상세 조회 응답 DTO")
public class PostDetailResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 제목", example = "결국 샀습니다")
    private String title;

    @Schema(description = "게시글 내용", example = "언젠가는 쓰겠죠..?")
    private String content;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/image1.jpg")
    private String thumbnailUrl;

    @Schema(description = "게시글 카테고리", example = "RANDOM_BUY")
    private String category;

    @Schema(description = "게시글 태그", example = "TREASURE")
    private String tag;

    @Schema(description = "좋아요 수", example = "12")
    private int likeCount;

    @Schema(description = "댓글 수", example = "4")
    private int commentCount;

    @Schema(description = "이미지 URL 목록", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private List<String> imageUrlList;

    @Schema(description = "게시글 생성 일시", example = "2025-05-21T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "작성자 정보")
    private UserInfo user;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
    }
}
