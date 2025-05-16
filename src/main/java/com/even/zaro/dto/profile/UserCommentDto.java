package com.even.zaro.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCommentDto {
    private Long postId;
    private String title;
    private String category;
    private String tag;
    private int likeCount;
    private int commentCount;

    // 댓글 정보
    private String commentContent;
    private LocalDateTime commentCreatedAt;
}
