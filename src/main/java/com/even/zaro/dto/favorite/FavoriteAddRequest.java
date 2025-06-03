package com.even.zaro.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteAddRequest {

    @Schema(description = "카카오지도 장소 Id", example = "314222")
    private long kakaoPlaceId;

    @Schema(description = "메모", example = "친구랑 가보고 싶은 감성카페")
    private String memo;

    @Schema(description = "장소 이름", example = "강남 삼겹살 집")
    private String placeName;

    @Schema(description = "주소", example = "강남의 삼겹살집 512")
    private String address;

    @Schema(description = "위도", example = "37.55123")
    private double lat;

    @Schema(description = "경도", example = "127.1234455")
    private double lng;

    @Schema(description = "장소 분류", example = "MT1")
    private String category;
}
