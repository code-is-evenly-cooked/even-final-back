package com.even.zaro.service;

import com.even.zaro.dto.map.MarkerInfoResponse;
import com.even.zaro.dto.map.PlaceResponse;
import com.even.zaro.entity.Favorite;
import com.even.zaro.entity.Place;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.map.MapException;
import com.even.zaro.global.exception.place.PlaceException;
import com.even.zaro.repository.FavoriteRepository;
import com.even.zaro.repository.MapQueryRepository;
import com.even.zaro.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class MapService {
    private final PlaceRepository placeRepository;
    private final FavoriteRepository favoriteRepository;
    private final MapQueryRepository mapQueryRepository;

    public MarkerInfoResponse getPlaceInfo(long placeId) {

        Place selectPlace = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceException(ErrorCode.PLACE_NOT_FOUND));

        // 해당 지역에 메모를 남긴 사용자들 리스트를 가져와야 함.
        List<Favorite> allByPlace = favoriteRepository.findAllByPlace(selectPlace);

        // 유저 요약 정보 순회하며 리스트에 저장
        List<MarkerInfoResponse.UserSimpleResponse> userSimpleResponses = allByPlace.stream()
                .map(fav -> MarkerInfoResponse.UserSimpleResponse.builder()
                        .userId(fav.getUser().getId())
                        .profileImage(fav.getUser().getProfileImage())
                        .nickname(fav.getUser().getNickname())
                        .memo(fav.getMemo())
                        .build())
                .toList();

        // 생성자를 이용해 응답하도록 수정
        MarkerInfoResponse markerInfo = new MarkerInfoResponse(
                selectPlace.getId(),
                selectPlace.getName(),
                selectPlace.getAddress(),
                selectPlace.getLat(),
                selectPlace.getLng(),
                selectPlace.getFavoriteCount(),
                userSimpleResponses
        );


        return markerInfo;
    }

    public PlaceResponse getPlacesByCoordinate(double lat, double lng, double distanceKm) {

        List<Place> placeByCoordinate = mapQueryRepository.findPlaceByCoordinate(lat, lng, distanceKm);

        // 조회된 장소가 없을 때
        if (placeByCoordinate.isEmpty()) {
            throw new MapException(ErrorCode.BY_COORDINATE_NOT_FOUND_PLACE_LIST);
        }

        List<PlaceResponse.PlaceInfo> placeInfos =  placeByCoordinate.stream()
                .map(place -> PlaceResponse.PlaceInfo.builder()
                        .place_id(place.getId())
                        .name(place.getName())
                        .address(place.getAddress())
                        .lat(place.getLat())
                        .lng(place.getLng())
                        .build()
                ).toList();

        int totalCount = placeByCoordinate.size();

        PlaceResponse placeResponse = PlaceResponse.builder()
                .totalCount(totalCount)
                .placeInfos(placeInfos)
                .build();

        return placeResponse;
    }
}
