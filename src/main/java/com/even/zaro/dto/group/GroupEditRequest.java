package com.even.zaro.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class GroupEditRequest {
    @Schema(description = "그룹 이름", example = "부산 맛집!~")
    private String name;
}
