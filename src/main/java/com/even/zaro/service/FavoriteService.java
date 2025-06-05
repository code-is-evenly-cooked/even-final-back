package com.even.zaro.service;

import com.even.zaro.dto.favorite.FavoriteAddRequest;
import com.even.zaro.dto.favorite.FavoriteAddResponse;
import com.even.zaro.dto.favorite.FavoriteEditRequest;
import com.even.zaro.dto.favorite.FavoriteResponse;
import com.even.zaro.entity.Favorite;
import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.Place;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.favorite.FavoriteException;
import com.even.zaro.global.exception.group.GroupException;
import com.even.zaro.global.exception.map.MapException;
import com.even.zaro.global.exception.place.PlaceException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.mapper.FavoriteMapper;
import com.even.zaro.repository.FavoriteGroupRepository;
import com.even.zaro.repository.FavoriteRepository;
import com.even.zaro.repository.PlaceRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteGroupRepository favoriteGroupRepository;
    private final FavoriteMapper favoriteMapper;

    // 그룹에 즐겨찾기를 추가
    public FavoriteAddResponse addFavorite(long groupId, FavoriteAddRequest request, long userId) {

        FavoriteGroup group = favoriteGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        // 해당 장소가 이미 저장되어있는지 없다면 장소 추가
        Place place = checkDuplicateByKakoPlaceId(request);

        // 해당 유저가 이미 그 장소를 추가했는지 확인
        boolean check = favoriteRepository.existsByPlaceAndUser(place, user);

        // 해당 유저가 placeId가 일치하는 장소가 이미 추가되어있다면
        if (check) {
            throw new FavoriteException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .group(group)
                .place(place)
                .memo(request.getMemo())
                .isDeleted(false)
                .build();

        // 즐겨찾기 개수 1 증가
        place.incrementFavoriteCount();

        // 그룹의 즐겨찾기 개수 1 증가
        group.incrementFavoriteCount();

        favoriteRepository.save(favorite);

        return favoriteMapper.toFavoriteAddResponse(favorite);
    }

    // 해당 그룹의 즐겨찾기 리스트를 조회
    public List<FavoriteResponse> getGroupItems(long groupId) {
        FavoriteGroup group = favoriteGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND));

        // 삭제된 데이터 제외하고 조회
        List<Favorite> activeFavoriteList = favoriteRepository.findAllByGroupAndIsDeletedFalse(group);

        if (activeFavoriteList.isEmpty()) {
            throw new FavoriteException(ErrorCode.FAVORITE_LIST_NOT_FOUND);
        }

        return favoriteMapper.toFavoriteResponseList(activeFavoriteList);
    }

    // 해당 즐겨찾기의 메모를 수정
    public void editFavoriteMemo(long favoriteId, FavoriteEditRequest request, long userId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new FavoriteException(ErrorCode.FAVORITE_NOT_FOUND));

        // 로그인한 사용자와 즐겨찾기의 userId가 일치하는지 검증
        if (favorite.getUser().getId() != userId) {
            throw new FavoriteException(ErrorCode.UNAUTHORIZED_FAVORITE_UPDATE);
        }

        favorite.editMemo(request.getMemo());
    }

    // 해당 즐겨찾기를 soft 삭제
    public void deleteFavorite(long favoriteId, long userId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new FavoriteException(ErrorCode.FAVORITE_NOT_FOUND));

        // 즐겨찾기의 userId와 로그인한 사용자의 userId 일치 여부 검증
        if (favorite.getUser().getId() != userId) {
            throw new FavoriteException(ErrorCode.UNAUTHORIZED_FAVORITE_DELETE);
        }

        long placeId = favorite.getPlace().getId();

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new MapException(ErrorCode.PLACE_NOT_FOUND));

        // 즐겨찾기 개수 1 감소
        place.decrementFavoriteCount();

        // 해당 즐겨찾기가 포함된 그룹
        FavoriteGroup group = favoriteGroupRepository.findById(favorite.getGroup().getId())
                .orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND));

        // 그룹의 즐겨찾기 개수 1 감소
        group.decrementFavoriteCount();

        // 삭제 상태 변경
        favorite.setDeleteTrue();
    }


    // Place 테이블에 해당 kakaoPlaceId를 가진 데이터 존재 여부 검증
    Place checkDuplicateByKakoPlaceId(FavoriteAddRequest request) {
        Optional<Place> getPlace = placeRepository.findByKakaoPlaceId(request.getKakaoPlaceId());

        // 장소가 이미 추가되어있지 않았다면, 그 장소를 DB에 저장
        return getPlace.orElseGet(() ->
                placeRepository.save(Place.builder()
                        .kakaoPlaceId(request.getKakaoPlaceId())
                        .name(request.getPlaceName())
                        .lat(request.getLat())
                        .lng(request.getLng())
                        .category(request.getCategory())
                        .address(request.getAddress())
                        .build())
        );
    }

    public boolean checkFavorite(long userId, long placeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceException(ErrorCode.PLACE_NOT_FOUND));


        // 해당 유저가 이미 그 장소를 추가했는지 확인
        boolean check = favoriteRepository.existsByPlaceAndUser(place, user);

        return check;
    }
}
