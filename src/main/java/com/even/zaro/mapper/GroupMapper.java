package com.even.zaro.mapper;

import com.even.zaro.dto.favorite.FavoriteResponse;
import com.even.zaro.dto.group.GroupResponse;
import com.even.zaro.entity.Favorite;
import com.even.zaro.entity.FavoriteGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(source = "group.id", target = "groupId")
    GroupResponse toGroupResponse(FavoriteGroup group);

    default List<GroupResponse> toGroupResponseList(List<FavoriteGroup> groupList) {
        return groupList.stream()
                .map(this::toGroupResponse)
                .toList();
    }
}
