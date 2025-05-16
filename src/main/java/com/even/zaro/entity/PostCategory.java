package com.even.zaro.entity;

import java.util.List;

public enum PostCategory {
    TOGETHER(List.of(PostTag.GROUP_BUY, PostTag.SHARING, PostTag.EXCHANGE)),   //같이쓰자
    DAILY_LIFE(List.of(PostTag.TIPS, PostTag.QUESTIONS)), //자취일상
    RANDOM_BUY(List.of(PostTag.TREASURE, PostTag.REGRET));//아무거나 샀어요

    private final List<PostTag> allowedTags;

    PostCategory(List<PostTag> allowedTags) {
        this.allowedTags = allowedTags;
    }

    public boolean isAllowed(PostTag tag) {
        return allowedTags.contains(tag);
    }
}
