package com.even.zaro.global.elasticsearch.document;

import com.even.zaro.entity.Post;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.ZoneOffset;

@Document(indexName = "posts")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEsDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String thumbnailImage;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String tag;

    @Field(type = FieldType.Integer)
    private int likeCount;

    @Field(type = FieldType.Integer)
    private int commentCount;

    @Field(type = FieldType.Keyword)
    private String createdAt;

    public static PostEsDocument from(Post post) {

        String cleanContent = post.getContent()
                .replaceAll("!\\[.*?]\\(.*?\\)", "")
                .replaceAll("<[^>]*>", "")
                .replaceAll("\\s+", " ")
                .trim();

        return PostEsDocument.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(cleanContent)
                .thumbnailImage(post.getThumbnailImage())
                .category(post.getCategory().name())
                .tag(post.getTag().name())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt().atOffset(ZoneOffset.UTC).toString())
                .build();
    }
}
