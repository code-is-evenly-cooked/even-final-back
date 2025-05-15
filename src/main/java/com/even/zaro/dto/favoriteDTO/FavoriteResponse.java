package com.even.zaro.dto.favoriteDTO;

import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.Place;
import com.even.zaro.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FavoriteResponse {
    private long id;

    private long userId;

    private long groupId;

    private long placeId;

    private double lat;

    private double lng;

    private String memo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isDeleted;
}
