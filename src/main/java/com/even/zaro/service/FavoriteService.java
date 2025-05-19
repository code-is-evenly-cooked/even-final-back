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
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteGroupRepository favoriteGroupRepository;

    // 그룹에 즐겨찾기를 추가
    public FavoriteAddResponse addFavorite(long groupId, FavoriteAddRequest request, long userId) {

        FavoriteGroup group = favoriteGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.EXAMPLE_USER_NOT_FOUND));

        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new MapException(ErrorCode.PLACE_NOT_FOUND));

        List<Favorite> placeList = favoriteRepository.findByPlace(place);

        // 해당 placeId와 일치하는 장소가 이미 추가되어있다면
        if (placeList.size() > 0) {
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

        favoriteRepository.save(favorite);

        FavoriteAddResponse favoriteAddResponse = FavoriteAddResponse.builder()
                .placeId(favorite.getPlace().getId())
                .memo(favorite.getMemo())
                .lat(favorite.getPlace().getLat())
                .lng(favorite.getPlace().getLng())
                .address(favorite.getPlace().getAddress())
                .build();

        return favoriteAddResponse;
    }

    // 해당 그룹의 즐겨찾기 리스트를 조회
    public List<FavoriteResponse> getGroupItems(long groupId) {
        FavoriteGroup group = favoriteGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(ErrorCode.GROUP_NOT_FOUND));

        List<Favorite> favoriteList = favoriteRepository.findAllByGroup(group);

        List<FavoriteResponse> favoriteResponseList = favoriteList.stream().map(favorite ->
                FavoriteResponse.builder()
                        .id(favorite.getId())
                        .userId(favorite.getUser().getId())
                        .groupId(favorite.getGroup().getId())
                        .placeId(favorite.getPlace().getId())
                        .memo(favorite.getMemo())
                        .createdAt(favorite.getCreatedAt())
                        .updatedAt(favorite.getUpdatedAt())
                        .isDeleted(favorite.isDeleted())
                        .lat(favorite.getPlace().getLat())
                        .lng(favorite.getPlace().getLng())
                        .address(favorite.getPlace().getAddress())
                        .build()
        ).toList();

        return favoriteResponseList;
    }

    // 해당 즐겨찾기의 메모를 수정
    public void editFavoriteMemo(long favoriteId, FavoriteEditRequest request, long userId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new FavoriteException(ErrorCode.FAVORITE_NOT_FOUND));

        // 로그인한 사용자와 즐겨찾기의 userId가 일치하는지 검증
        if (favorite.getUser().getId() != userId) {
            throw new FavoriteException(ErrorCode.UNAUTHORIZED_FAVORITE_UPDATE);
        }

        favorite.setMemo(request.getMemo());
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
        // 삭제 상태 변경
        favorite.setDeleteTrue();
    }
}
