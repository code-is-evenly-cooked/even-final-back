package com.even.zaro.dto.favoriteDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

@Getter
public class FavoriteAddRequest {

    @Schema(description = "사용자 ID", example = "1")
    private long userId;

    @Schema(description = "장소 ID", example = "3")
    private long placeId;

    @Schema(description = "메모", example = "친구랑 가보고 싶은 감성카페")
    private String memo;
}
