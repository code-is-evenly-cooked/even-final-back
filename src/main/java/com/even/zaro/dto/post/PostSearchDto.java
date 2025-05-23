package com.even.zaro.dto.post;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import java.time.LocalDateTime;


@JsonPropertyOrder({
        "postId",
        "title",
        "contentPreview",
        "thumbnailUrl",
        "category",
        "tag",
        "likeCount",
        "commentCount",
        "createdAt"
})
@Getter
public class PostSearchDto {
    private final Long postId;
    private final String title;
    private final String contentPreview;
    private final String thumbnailUrl;
    private final String category;
    private final String tag;
    private final int likeCount;
    private final int commentCount;
    private final LocalDateTime createdAt;

    public PostSearchDto(Long postId, String title, String content, String thumbnailUrl,
                         String category, String tag, int likeCount, int commentCount, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.contentPreview = truncate(content, 50);
        this.thumbnailUrl = thumbnailUrl;
        this.category = category;
        this.tag = tag;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
    }

    private String truncate(String content, int maxLength) {
        if (content == null) return "";
        return content.length() <= maxLength ? content : content.substring(0, maxLength) + "...";
    }
}