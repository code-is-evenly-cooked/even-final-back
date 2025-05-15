package com.even.zaro.dto.favoriteDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteAddResponse {

    @Schema(description = "장소 id", example = "1")
    private long placeId;

    @Schema(description = "즐겨찾기 메모", example = "친구랑 가보고 싶은 곳")
    private String memo;

    @Schema(description = "위도", example = "37.55812")
    private double lat;

    @Schema(description = "경도", example = "126.90543")
    private double lng;
}
