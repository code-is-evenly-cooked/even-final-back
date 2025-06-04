package com.even.zaro.mapper;

import com.even.zaro.dto.favorite.FavoriteAddResponse;
import com.even.zaro.dto.favorite.FavoriteResponse;
import com.even.zaro.dto.map.MarkerInfoResponse;
import com.even.zaro.entity.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    @Mapping(source = "favorite.place.id", target = "placeId")
    @Mapping(source = "favorite.place.lat", target = "lat")
    @Mapping(source = "favorite.place.lng", target = "lng")
    @Mapping(source = "favorite.place.address", target = "address")
    FavoriteAddResponse toFavoriteAddResponse(Favorite favorite);

    @Mapping(source = "favorite.user.id", target = "userId")
    @Mapping(source = "favorite.group.id", target = "groupId")
    @Mapping(source = "favorite.place.id", target = "placeId")
    @Mapping(source = "favorite.place.lat", target = "lat")
    @Mapping(source = "favorite.place.lng", target = "lng")
    @Mapping(source = "favorite.place.address", target = "address")
    @Mapping(source = "favorite.place.name", target = "placeName")
    @Mapping(target = "isDeleted", expression = "java(favorite.isDeleted())")
    FavoriteResponse toFavoriteResponse(Favorite favorite);

    default List<FavoriteResponse> toFavoriteResponseList(List<Favorite> favorites) {
        return favorites.stream()
                .map(this::toFavoriteResponse)
                .toList();
    }
}
