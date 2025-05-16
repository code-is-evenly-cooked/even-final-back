package com.even.zaro.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GroupCreateRequest {

    @Schema(description = "유저 id", example = "1")
    private long userId;

    // Group 이름
    @Schema(description = "그룹 이름", example = "강릉 맛집!!")
    private String name;
}
