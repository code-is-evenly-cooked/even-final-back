package com.even.zaro.dto.post;

import com.even.zaro.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "실시간 인기 게시글 응답 DTO")
public class PostRankResponseDto {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 제목", example = "레몬 사세요 🍋")
    private String title;

    @Schema(description = "좋아요 수", example = "5")
    private int likeCount;

    @Schema(description = "댓글 수", example = "3")
    private int commentCount;

    public static PostRankResponseDto from(Post post) {
        return PostRankResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .build();
    }
}
