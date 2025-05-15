package com.even.zaro.dto.profileDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupEditRequest {
    @Schema(description = "그룹 이름", example = "부산 맛집!~")
    private String name;
}
