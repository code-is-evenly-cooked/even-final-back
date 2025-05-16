package com.even.zaro.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

    @NotBlank
    private String title;

    private String content;

    @NotNull
    private String category;

    @NotNull
    private String tag;

    private List<String> imageUrlList;
    private String thumbnailUrl;
}
