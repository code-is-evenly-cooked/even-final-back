package com.even.zaro.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class GroupResponse {

    @Schema(description = "그룹 id", example = "1")
    private long groupId;

    @Schema(description = "그룹 이름", example = "부산 맛집!~")
    private String name;

    @Schema(description = "삭제 여부", example = "true")
    private boolean isDeleted;

    @Schema(description = "생성 시각", example = "2025-03-03T08:20:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2025-03-25T05:20:00")
    private LocalDateTime updatedAt;

    @Schema(description = "그룹의 즐겨찾기 개수", example = "2")
    private int groupFavoriteCount;
}
