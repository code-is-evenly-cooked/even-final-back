package com.even.zaro.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GroupEditRequest {
    @Schema(description = "그룹 이름", example = "부산 맛집!~")
    private String name;
}
