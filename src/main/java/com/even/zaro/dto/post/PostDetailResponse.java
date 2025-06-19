package com.even.zaro.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(description = "게시글 상세 조회 응답 DTO")
public class PostDetailResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 제목", example = "결국 샀습니다")
    private String title;

    @Schema(description = "게시글 내용", example = "언젠가는 쓰겠죠..?")
    private String content;

    @Schema(description = "썸네일 이미지 key", example = "/images/post/uuid1.png")
    private String thumbnailImage;

    @Schema(description = "게시글 카테고리", example = "RANDOM_BUY")
    private String category;

    @Schema(description = "게시글 태그", example = "TREASURE")
    private String tag;

    @Schema(description = "좋아요 수", example = "12")
    private int likeCount;

    @Schema(description = "댓글 수", example = "4")
    private int commentCount;

    @Schema(description = "포스트 이미지 key 리스트", example = "[\"/images/post/uuid1.png\", \"/images/post/uuid2.png\"]")
    private List<String> postImageList;

    @Schema(description = "게시글 생성 일시", example = "2025-05-21T10:15:30.111Z")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            timezone = "UTC"
    )
    private OffsetDateTime createdAt;

    @Schema(description = "작성자 정보")
    private UserInfo user;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String profileImage;
        private LocalDate liveAloneDate;
        private boolean following;
    }
}
