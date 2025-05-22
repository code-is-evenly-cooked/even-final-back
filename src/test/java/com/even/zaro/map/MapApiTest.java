package com.even.zaro.map;

import com.even.zaro.dto.favorite.FavoriteAddRequest;
import com.even.zaro.dto.group.GroupCreateRequest;
import com.even.zaro.dto.map.MarkerInfoResponse;
import com.even.zaro.dto.map.MarkerInfoResponse.UserSimpleResponse;
import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.place.PlaceException;
import com.even.zaro.repository.*;
import com.even.zaro.service.FavoriteService;
import com.even.zaro.service.GroupService;
import com.even.zaro.service.MapService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MapApiTest {

    @Autowired
    PlaceRepository placeRepository;
    @Autowired
    FavoriteRepository favoriteRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    FavoriteService favoriteService;
    @Autowired
    private MapService mapService;
    @Autowired
    private FavoriteGroupRepository favoriteGroupRepository;


    @Test
    void 마커_정보_조회_성공_테스트() {
        // Given : 유저를 생성하고 각 유저가 그룹에 즐겨찾기를 추가하는 예시
        User user1 = createUser("test1@naver.com", "Test1234!", "test1");
        User user2 = createUser("test2@naver.com", "Test1234!", "test2");

        List<Long> users = userRepository.findAll().stream().map(User::getId).toList();

            // 장소 1개 추가
        Place place = createPlace(1, "test1", "test1", 30, 42);

            // 유저 당 그룹 1개씩 추가
        craeteFavoriteGroup(user1.getId(), "test1");
        craeteFavoriteGroup(user2.getId(), "test2");

        List<Long> groupIds = favoriteGroupRepository.findAll().stream().map(FavoriteGroup::getId).toList();

            // 각 그룹에 장소를 즐겨찾기에 추가
        addFavoriteGroup(place.getId(), groupIds.getFirst(), "맛 없어요", users.getFirst());
        addFavoriteGroup(place.getId(), groupIds.getLast(), "맛 있어요", users.getLast());

        List<Long> favoriteIds = favoriteRepository.findAll().stream().map(Favorite::getId).toList();

        List<Favorite> favoriteList = favoriteRepository.findAllById(List.of(favoriteIds.getFirst(), favoriteIds.getLast()));

            // MarkerInfoResponse 내부 객체 UserSimpleResponse 객체 생성
        List<UserSimpleResponse> userSimpleResponses = favoriteList.stream().map(favorite -> UserSimpleResponse.builder()
                .profileImage(favorite.getUser().getProfileImage())
                .userId(favorite.getUser().getId())
                .nickname(favorite.getUser().getNickname())
                .memo(favorite.getMemo())
                .build()).toList();


        Place findPlace = placeRepository.findById(place.getId())
                .orElseThrow(() -> new PlaceException(ErrorCode.PLACE_NOT_FOUND));

            // 예상 응답 객체 사전 생성
        MarkerInfoResponse markerInfoResponse = MarkerInfoResponse.builder()
                .placeId(findPlace.getId())
                .placeName(findPlace.getName())
                .lat(findPlace.getLat())
                .lng(findPlace.getLng())
                .address(findPlace.getAddress())
                .usersInfo(userSimpleResponses)
                .build();

        // When : 실제 서비스 호출
        MarkerInfoResponse placeInfo = mapService.getPlaceInfo(favoriteIds.get(0));

        // Then : 호출 응답 결과와 사전 예상 응답객체와 필드별 비교
        assertThat(placeInfo.getPlaceId()).isEqualTo(markerInfoResponse.getPlaceId());
        assertThat(placeInfo.getPlaceName()).isEqualTo(markerInfoResponse.getPlaceName());
        assertThat(placeInfo.getLat()).isEqualTo(markerInfoResponse.getLat());
        assertThat(placeInfo.getLng()).isEqualTo(markerInfoResponse.getLng());
        assertThat(placeInfo.getAddress()).isEqualTo(markerInfoResponse.getAddress());

        // usersInfo 리스트 비교 (필드까지 깊이 비교)
        assertThat(placeInfo.getUsersInfo())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(markerInfoResponse.getUsersInfo());
    }

    // 임시 유저 생성 메서드
    User createUser(String email, String password, String nickname) {
        return userRepository.save(User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .status(Status.PENDING)
                .build());
    }

    // 장소 추가 메서드
    Place createPlace(long kakaoPlaceId, String name, String address, double lat, double lng) {
        return placeRepository.save(Place.builder()
                .kakaoPlaceId(kakaoPlaceId)
                .name(name)
                .address(address)
                .lat(lat)
                .lng(lng)
                .build());
    }


    // 그룹 추가 메서드
    void craeteFavoriteGroup(long userId, String groupName) {
        GroupCreateRequest request = GroupCreateRequest.builder().name(groupName).build();
        groupService.createGroup(request, userId);
    }

    // 그룹에 즐겨찾기 장소 추가
    void addFavoriteGroup(long placeId, long groupId, String memo, long userId) {
        // 해당 그룹에 즐겨찾기 장소 추가
        FavoriteAddRequest request = FavoriteAddRequest.builder()
                .placeId(placeId)
                .memo(memo)
                .build();

        favoriteService.addFavorite(groupId, request, userId);
    }

}
