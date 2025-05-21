package com.even.zaro.dto.post;

import com.even.zaro.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "좋아요한 게시글 응답")
@Getter
@AllArgsConstructor
public class PostLikeResponse {
    @Schema(description = "게시글 ID")
    private Long postId;

    @Schema(description = "게시글 제목")
    private String title;

    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "썸네일 URL")
    private String thumbnailUrl;

    @Schema(description = "좋아요 수")
    private int likeCount;

    public static PostLikeResponse from(Post post) {
        return new PostLikeResponse(
                post.getId(),
                post.getTitle(),
                contentPreview(post.getContent()),
                post.getThumbnailUrl(),
                post.getLikeCount()
        );
    }

    private static String contentPreview(String content) {
        return content.length() <= 30 ? content : content.substring(0, 30) + "...";
    }
}