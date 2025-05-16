package com.even.zaro.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PostListResponse {
    private List<PostPreviewDto> posts;
    private PageInfo pageInfo;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PageInfo {
        private int currentPage;
        private int totalPages;
        private int totalElements;
    }
}