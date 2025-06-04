package com.even.zaro.mapper;

import com.even.zaro.dto.map.MarkerInfoResponse;
import com.even.zaro.entity.Favorite;
import com.even.zaro.entity.Place;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MapMapper {
    MapMapper INSTANCE = Mappers.getMapper(MapMapper.class);

    @Mapping(source = "place.id", target = "placeId")
    @Mapping(source = "place.name", target = "placeName")
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
