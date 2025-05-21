package com.even.zaro.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "게시글 생성 요청 DTO")
public class PostCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Schema(description = "게시글 제목", example = "오늘의 자취 꿀템 추천!")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Schema(description = "게시글 내용", example = "자취 꿀템 공유해요~")
    private String content;

    @NotNull
    @Schema(description = "게시글 카테고리", example = "DAILY_LIFE")
    private String category;

    @NotNull
    @Schema(description = "게시글 태그", example = "TIP")
    private String tag;

    @Schema(description = "이미지 URL 목록", example = "https://s3.bucket.com/image1.png")
    private List<String> imageUrlList;

    @Schema(description = "썸네일 이미지 URL", example = "https://s3.bucket.com/image1.png")
    private String thumbnailUrl;
}
