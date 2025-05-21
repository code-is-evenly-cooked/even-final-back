package com.even.zaro.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteEditRequest {
    @Schema(description = "즐겨찾기 메모", example = "주말에 친구랑 가보고 싶은 감성카페")
    private String memo;
}
