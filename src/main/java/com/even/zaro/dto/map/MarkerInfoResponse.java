package com.even.zaro.dto.map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class MarkerInfoResponse {

    long placeId;

    String placeName;

    String address;

    double lat;
    double lng;

    String category;

    int favoriteCount;

    List<UserSimpleResponse> usersInfo;

    @Builder
    @Getter
    @AllArgsConstructor
    public static class UserSimpleResponse {
        long userId;

        private String profileImage;

        private String nickname;

        private String memo;
    }
}


