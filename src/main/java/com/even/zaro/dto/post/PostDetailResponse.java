package com.even.zaro.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PostDetailResponse {

    private Long postId;
    private String title;
    private String content;
    private String thumbnailUrl;
    private String category;
    private String tag;
    private int likeCount;
    private int commentCount;
    private List<String> imageUrlList;
    private LocalDateTime createdAt;

    private UserInfo user;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
    }
}
