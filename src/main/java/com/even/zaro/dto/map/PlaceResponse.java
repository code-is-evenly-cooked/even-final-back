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
        long place_id;
        String name;
        String address;
        double lat;
        double lng;
    }

}
