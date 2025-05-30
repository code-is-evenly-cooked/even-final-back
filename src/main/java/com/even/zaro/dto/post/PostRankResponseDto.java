package com.even.zaro.dto.post;

import com.even.zaro.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostRankResponseDto {
    private Long postId;
    private String title;
    private int likeCount;
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
