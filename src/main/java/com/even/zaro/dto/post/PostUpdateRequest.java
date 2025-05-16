package com.even.zaro.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String tag;
    private List<String> imageUrlList;
    private String thumbnailUrl;
}
