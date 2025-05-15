package com.even.zaro.dto.profileDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupEditRequest {
    @Schema(description = "그룹 id", example = "1")
    private long groupId;

    @Schema(description = "그룹 이름", example = "부산 맛집!~")
    private String name;

    @Schema(hidden = true) // Swagger UI 에서 필드 숨김
    private LocalDateTime updatedAt;
}
