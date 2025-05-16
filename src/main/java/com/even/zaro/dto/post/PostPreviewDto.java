package com.even.zaro.dto.post;

import com.even.zaro.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PostPreviewDto {
    private Long postId;
    private String title;
    private String content;
    private String thumbnailUrl;
    private String category;
    private String tag;
    private int likeCount;
    private int commentCount;
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
