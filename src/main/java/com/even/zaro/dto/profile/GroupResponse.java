package com.even.zaro.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GroupResponse {

    @Schema(description = "그룹 id", example = "1")
    private long id;

    @Schema(description = "그룹 이름", example = "부산 맛집!~")
    private String name;
}
