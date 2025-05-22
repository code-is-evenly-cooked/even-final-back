package com.even.zaro.service;

import com.even.zaro.dto.map.MarkerInfoResponse;
import com.even.zaro.dto.map.MarkerInfoResponse.UserSimpleResponse;
import com.even.zaro.entity.Place;
import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.repository.FavoriteRepository;
import com.even.zaro.repository.MapQueryRepository;
import com.even.zaro.repository.PlaceRepository;
import com.even.zaro.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;


import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class MapServiceTest {

    @InjectMocks
    MapService mapService;

    @Mock
    PlaceRepository placeRepository;

    @Mock
    FavoriteRepository favoriteRepository;

    @Mock
    MapQueryRepository mapQueryRepository;

    @Mock
    UserRepository userRepository;


    @DisplayName("장소 정보 조회 성공")
    @Test
    void getPlaceInfo_success() {
        // given
        long placeId = 1L;
        Place mockPlace = Place.builder()
                .id(placeId)
                .name("서울역")
                .lat(37)
                .lng(42)
                .address("서울 중구 한강대로")
                .build();

        List<UserSimpleResponse> userSimpleResponses = List.of(
                new UserSimpleResponse(1L, "img1", "nick1", "memo1"),
                new UserSimpleResponse(2L, "img2", "nick2", "memo2"));

        MarkerInfoResponse markerInfoResponse = MarkerInfoResponse.builder()
                .placeId(mockPlace.getId())
                .placeName(mockPlace.getName())
                .lat(mockPlace.getLat())
                .lng(mockPlace.getLng())
                .address(mockPlace.getAddress())
                .usersInfo(userSimpleResponses)
                .build();

        when(placeRepository.findById(1L)).thenReturn(Optional.of(mockPlace));
        when(mapService.getPlaceInfo(1L)).thenReturn(markerInfoResponse);
    }
}
