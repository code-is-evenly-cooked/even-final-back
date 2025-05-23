package com.even.zaro.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupCreateRequest {
    // Group 이름
    @Schema(description = "그룹 이름", example = "강릉 맛집!!")
    private String groupName;
}
