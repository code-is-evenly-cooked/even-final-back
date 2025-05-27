package com.even.zaro.dto.post;

import com.even.zaro.entity.Post;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "게시글 목록 응답 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostPreviewDto {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "게시글 제목", example = "오늘의 자취 꿀템 공유")
    private String title;

    @Schema(description = "미리보기용 게시글 내용", example = "이건 제가 진짜 매일 쓰는 꿀템인데요...")
    private String content;

    @Schema(description = "썸네일 이미지 key", example = "/images/post/uuid1.png")
    private String thumbnailImage;

    @Schema(description = "게시글 카테고리", example = "DAILY_LIFE")
    private String category;

    @Schema(description = "게시글 태그", example = "TIPS")
    private String tag;

    @Schema(description = "좋아요 수", example = "5")
    private int likeCount;

    @Schema(description = "댓글 수", example = "3")
    private int commentCount;

    @Schema(description = "작성자 프로필 이미지 URL", example = "https://your-cdn.com/default.png", nullable = true)
    private String writerProfileImage;

    @Schema(description = "작성자 닉네임", example = "이브니쨩", nullable = true)
    private String writerNickname;


    @Schema(description = "게시글 생성 일시", example = "2025-05-21T12:34:56")
    private LocalDateTime createdAt;

    public static PostPreviewDto from(Post post) {
        PostPreviewDto.PostPreviewDtoBuilder builder = PostPreviewDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .thumbnailImage(post.getThumbnailImage())
                .category(post.getCategory().name())
                .tag(post.getTag() != null ? post.getTag().name() : null)
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt());

        if (post.getCategory() == Post.Category.RANDOM_BUY){
            builder.writerProfileImage(post.getUser().getProfileImage());
            builder.writerNickname(post.getUser().getNickname());
        }

        return builder.build();

    }

}
