package com.even.zaro.dto.map;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PlaceResponse {

    int totalCount;

    List<PlaceInfo> placeInfos;

    @Builder
    @Getter
    static public class PlaceInfo {
        long placeId;
        long kakaoPlaceId;
        String name;
        String address;
        String category;
        double lat;
        double lng;
        int favoriteCount;
    }

}
