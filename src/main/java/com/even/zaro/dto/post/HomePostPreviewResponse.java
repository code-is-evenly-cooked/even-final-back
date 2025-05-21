package com.even.zaro.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "홈화면 게시글 미리보기 응답 DTO")
public class HomePostPreviewResponse {

    @Schema(description = "같이쓰기 카테고리의 게시글 미리보기 리스트")
    private List<SimplePostDto> together;

    @Schema(description = "자취일상 카테고리의 게시글 미리보기 리스트")
    private List<SimplePostDto> dailyLife;

    @Schema(description = "아무거나샀어요 카테고리의 인기 게시글 미리보기 리스트")
    private List<RandomBuyPostDto> randomBuy;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SimplePostDto {
        private Long postId;
        private String title;
        private String createAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RandomBuyPostDto {
        private Long postId;
        private String title;
        private String content;
        private String thumbnailUrl;
        private int likeCount;
        private int commentCount;
        private String writerProfileImage;
        private String writerNickname;
        private String createAt;

    }

}
