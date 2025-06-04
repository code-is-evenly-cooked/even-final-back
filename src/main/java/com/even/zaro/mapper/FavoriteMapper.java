package com.even.zaro.mapper;

import com.even.zaro.dto.favorite.FavoriteAddResponse;
import com.even.zaro.entity.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    @Mapping(source = "favorite.place.id", target = "placeId")
    @Mapping(source = "favorite.place.lat", target = "lat")
    @Mapping(source = "favorite.place.lng", target = "lng")
    @Mapping(source = "favorite.place.address", target = "address")
    FavoriteAddResponse toFavoriteAddResponse(Favorite favorite);
}
