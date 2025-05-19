package com.even.zaro.dto.map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class MarkerInfoResponse {

    long placeId;

    String placeName;

    String address;

    double lat;
    double lng;

    int favoriteCount;

    List<UserSimpleResponse> usersInfo;

    @Builder
    @Setter
    @Getter
    public static class UserSimpleResponse {
        long userId;

        private String profileImage;

        private String nickname;

        private String memo;
    }
}


