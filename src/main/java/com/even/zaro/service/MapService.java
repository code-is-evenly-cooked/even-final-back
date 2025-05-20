package com.even.zaro.service;

import com.even.zaro.dto.map.MarkerInfoResponse;
import com.even.zaro.entity.Favorite;
import com.even.zaro.entity.Place;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.place.PlaceException;
import com.even.zaro.repository.FavoriteRepository;
import com.even.zaro.repository.PlaceRepository;
import com.even.zaro.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class MapService {
    private final PlaceRepository placeRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public MarkerInfoResponse getPlaceInfo(long placeId) {

        Place selectPlace = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceException(ErrorCode.PLACE_NOT_FOUND));

        // 해당 지역에 메모를 남긴 사용자들 리스트를 가져와야 함.
        List<Favorite> allByPlace = favoriteRepository.findAllByPlace(selectPlace);

//        // 사용자들의 id 리스트를 저장
//        List<Long> userIds = allByPlace.stream()
//                .map(fav -> fav.getUser().getId())
//                .toList();
//
//        List<String> userMemos = allByPlace.stream()
//                .map(Favorite::getMemo)
//                .toList();

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
}
