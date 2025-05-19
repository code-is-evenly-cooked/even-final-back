package com.even.zaro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPostDto {
    private Long postId;
    private String title;
    private String content;
    private String category;
    private String tag;
    private String thumbnailUrl;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;
}