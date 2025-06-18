package com.even.zaro.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "게시글 수정 요청 DTO")
public class PostUpdateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Schema(description = "게시글 제목", example = "오늘의 자취 꿀템 추천!")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Schema(description = "게시글 내용", example = "자취 꿀템 공유해요~")
    private String content;

    @Schema(description = "게시글 태그", example = "TIPS")
    private String tag;

    @Schema(description = "포스트 이미지 key 리스트", example = "[\"/images/post/uuid1.png\", \"/images/post/uuid2.png\"]")
    private List<String> postImageList;

    @Schema(description = "썸네일 이미지 key", example = "/images/post/uuid1.png")
    private String thumbnailImage;

    public PostUpdateRequest(String title, String content, String tag, List<String> postImageList, String thumbnailImage) {
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.postImageList = postImageList;
        this.thumbnailImage = thumbnailImage;
    }
}
