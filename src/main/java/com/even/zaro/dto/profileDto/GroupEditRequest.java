package com.even.zaro.dto.profileDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupEditRequest {
    private long groupId;
    private String name;

    @Schema(hidden = true) // Swagger UI 에서 필드 숨김
    private LocalDateTime updatedAt;
}
