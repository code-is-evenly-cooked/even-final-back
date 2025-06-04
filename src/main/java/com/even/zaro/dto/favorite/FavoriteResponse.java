package com.even.zaro.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FavoriteResponse {

    @Schema(description = "즐겨찾기 id", example = "1")
    private long id;

    @Schema(description = "유저 id", example = "1")
    private long userId;

    @Schema(description = "그룹 id", example = "1")
    private long groupId;

    @Schema(description = "장소 id", example = "1")
    private long placeId;

    @Schema(description = "장소 이름", example = "육전식당")
    private String placeName;

    @Schema(description = "위도", example = "37.5665")
    private double lat;

    @Schema(description = "경도", example = "126.978")
    private double lng;

    @Schema(description = "즐겨찾기 메모", example = "주말에 친구랑 가보고 싶은 감성카페")
    private String memo;

    @Schema(description = "생성 시간", example = "2025-05-15 16:13:10")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간", example = "2025-05-15 16:13:10")
    private LocalDateTime updatedAt;

    @Schema(description = "주소", example = "서울특별시 중구 세종대로 110")
    private String address;

    @Schema(description = "삭제 여부", example = "0")
    private boolean isDeleted;
}
