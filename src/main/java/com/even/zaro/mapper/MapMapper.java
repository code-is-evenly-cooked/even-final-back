package com.even.zaro.mapper;

import com.even.zaro.dto.map.MarkerInfoResponse;
import com.even.zaro.entity.Favorite;
import com.even.zaro.entity.Place;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MapMapper {

    @Mapping(source = "place.id", target = "placeId")
    @Mapping(source = "place.name", target = "placeName")
    @Mapping(source = "place.address", target = "address")
    @Mapping(source = "place.lat", target = "lat")
    @Mapping(source = "place.lng", target = "lng")
    @Mapping(source = "place.category", target = "category")
    @Mapping(source = "place.favoriteCount", target = "favoriteCount")
    @Mapping(source = "userSimpleResponses", target = "usersInfo") // ❗ 명시적으로 매핑
    MarkerInfoResponse toMarkerInfoResponse(Place place, List<MarkerInfoResponse.UserSimpleResponse> userSimpleResponses);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.profileImage", target = "profileImage")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "memo", target = "memo")
    MarkerInfoResponse.UserSimpleResponse toUserSimpleResponse(Favorite favorite);

    default List<MarkerInfoResponse.UserSimpleResponse> toUserSimpleResponseList(List<Favorite> favorites) {
        return favorites.stream()
                .map(this::toUserSimpleResponse)
                .toList();
    }
}