package com.even.zaro.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class HomePostPreviewResponse {

    private List<SimplePostDto> together;
    private List<SimplePostDto> dailyLife;
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
