package com.even.zaro.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteAddRequest {
    @Schema(description = "장소 ID", example = "3")
    private long placeId;

    @Schema(description = "메모", example = "친구랑 가보고 싶은 감성카페")
    private String memo;
}
