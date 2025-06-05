package com.even.zaro.dto.post;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.OffsetDateTime;


@JsonPropertyOrder({
        "postId",
        "title",
        "contentPreview",
        "thumbnailImage",
        "category",
        "tag",
        "likeCount",
        "commentCount",
        "createdAt"
})

@Getter
public class PostSearchDto {

    @Schema(description = "게시글 ID", example = "1")
    private final Long postId;

    @Schema(description = "게시글 제목", example = "이사 꿀팁 공유해요")
    private final String title;

    @Schema(description = "게시글 내용", example = "이사할때 박스를 미리 사세요!")
    private final String contentPreview;

    @Schema(description = "썸네일 이미지 key", example = "/images/post/uuid1.png")
    private String thumbnailImage;

    @Schema(description = "카테고리명", example = "자취일상")
    private final String category;

    @Schema(description = "태그", example = "TIPS")
    private final String tag;

    @Schema(description = "좋아요 수", example = "3")
    private final int likeCount;

    @Schema(description = "댓글 수", example = "7")
    private final int commentCount;

    @Schema(description = "게시글 작성 시간", example = "2025-05-23T09:30:00.111Z")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            timezone = "UTC"
    )
    private final OffsetDateTime createdAt;

    public PostSearchDto(Long postId, String title, String content, String thumbnailImage,
                         String category, String tag, int likeCount, int commentCount, OffsetDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.contentPreview = truncate(content, 50);
        this.thumbnailImage = thumbnailImage;
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