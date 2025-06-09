package com.even.zaro.integration.map;

import com.even.zaro.dto.favorite.FavoriteAddRequest;
import com.even.zaro.dto.group.GroupCreateRequest;
import com.even.zaro.dto.map.MarkerInfoResponse;
import com.even.zaro.dto.map.MarkerInfoResponse.UserSimpleResponse;
import com.even.zaro.dto.map.PlaceResponse;
import com.even.zaro.dto.map.PlaceResponse.PlaceInfo;
import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.map.MapException;
import com.even.zaro.global.exception.place.PlaceException;
import com.even.zaro.repository.*;
import com.even.zaro.service.FavoriteService;
import com.even.zaro.service.GroupService;
import com.even.zaro.service.MapService;
import org.junit.jupiter.api.Assertions;
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
    @Autowired
    private MapQueryRepository mapQueryRepository;


    @Test
    void 마커_정보_조회_성공_테스트() {
        // Given : 유저를 생성하고 각 유저가 그룹에 즐겨찾기를 추가하는 예시
        User user1 = createUser("test1@naver.com", "Test1234!", "test1");
//        User user2 = createUser("test2@naver.com", "Test1234!", "test2");

        List<Long> users = userRepository.findAll().stream().map(User::getId).toList();

        long user1_Id = users.getFirst();
//        long user2_Id = users.getLast();

        // 유저 당 그룹 1개씩 추가
        createFavoriteGroup(user1.getId(), "test1");
//        createFavoriteGroup(user2.getId(), "test2");

        List<Long> groupIds = favoriteGroupRepository.findAll().stream().map(FavoriteGroup::getId).toList();
        long user1_groupId = groupIds.getFirst();
//        long user2_groupId = groupIds.getLast();

        // 각 그룹에 장소를 즐겨찾기에 추가
//        addFavoriteGroup(place.getId(), groupIds.getFirst(), "맛 없어요", users.getFirst());
//        addFavoriteGroup(place.getId(), groupIds.getLast(), "맛 있어요", users.getLast());
        addFavoriteGroup(1, "의정부 512", "의정부 맛집", "맛 없어요", 35.123, 123.321, user1_groupId, user1_Id);
//        addFavoriteGroup(2, "의정부 513", "의정부 맛집", "맛 있어요", 35.122, 123.325, user2_groupId, user2_Id);

        List<Long> favoriteIds = favoriteRepository.findAll().stream().map(Favorite::getId).toList();

        List<Favorite> favoriteList = favoriteRepository.findAllById(List.of(favoriteIds.getFirst(), favoriteIds.getLast()));

        // MarkerInfoResponse 내부 객체 UserSimpleResponse 객체 생성
        List<UserSimpleResponse> userSimpleResponses = favoriteList.stream().map(favorite -> UserSimpleResponse.builder()
                .profileImage(favorite.getUser().getProfileImage())
                .userId(favorite.getUser().getId())
                .nickname(favorite.getUser().getNickname())
                .memo(favorite.getMemo())
                .build()).toList();

        List<Long> placeIdList = placeRepository.findAll().stream().map(Place::getId).toList();



        Place findPlace = placeRepository.findById(placeIdList.getFirst())
                .orElseThrow(() -> new MapException(ErrorCode.PLACE_NOT_FOUND));

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
        MarkerInfoResponse placeInfo = mapService.getPlaceInfo(favoriteIds.getFirst());

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

    @Test
    void 좌표_기반_인근_장소_리스트_조회_성공_테스트() {
        // Given : 장소 추가
        createPlace(1001, "서울역", "서울 중구 한강대로 405", 37.554722, 126.970833);     // ✅ 기준 점
        createPlace(1002, "남대문시장", "서울 중구 남대문시장길", 37.559500, 126.975000); // ✅ 1KM 안
        createPlace(1003, "서울시청", "서울 중구 세종대로", 37.562000, 126.974000);       // ✅ 1KM 안
        createPlace(1004, "광화문", "서울 종로구 세종대로", 37.575000, 126.980000);       // ❌
        createPlace(1005, "신촌역", "서울 서대문구 신촌로", 37.556000, 126.936000);       // ❌
        createPlace(1006, "숙대입구역", "서울 용산구 청파로", 37.542000, 126.975000);     // ❌
        createPlace(1007, "강남역", "서울 강남구 강남대로", 37.498000, 127.028000);       // ❌

        // When : 서울역 기준으로 1KM 반경 장소 조회
        PlaceResponse placeByCoordinate = mapService.getPlacesByCoordinate(37.554722, 126.970833, 1);

        placeByCoordinate.getPlaceInfos().forEach(
                place -> {
                    double distance = calculateHaversine(37.554722, 126.970833, place.getLat(), place.getLng());
                    System.out.printf("📍 %s → %.2fkm\n", place.getName(), distance);
                });

        // Then : 1Km 반경 기준 데이터 검증
        List<String> names = placeByCoordinate.getPlaceInfos().stream().map(PlaceInfo::getName).toList();

        assertThat(names).containsExactlyInAnyOrder("서울역", "서울시청", "남대문시장"); // 1Km 안의 장소가 조회됐는지 검증
        assertThat(names).doesNotContain("광화문", "신촌역", "숙대입구역", "강남역"); // 반경 밖의 장소가 데이터에 포함이 안 됐는지 검증
    }

    @Test
    void 좌표_기반_인근_장소_리스트_조회_예외_테스트_BY_COORDINATE_NOT_FOUND_PLACE_LIST() {
        // Given : 장소 추가
        createPlace(1001, "서울역", "서울 중구 한강대로 405", 37.554722, 126.970833);     // ❌   서울
        createPlace(1002, "남대문시장", "서울 중구 남대문시장길", 37.559500, 126.975000); // ❌   서울
        createPlace(1003, "서울시청", "서울 중구 세종대로", 37.562000, 126.974000);       // ❌   서울
        createPlace(1004, "광화문", "서울 종로구 세종대로", 37.575000, 126.980000);       // ❌   서울
        createPlace(1005, "신촌역", "서울 서대문구 신촌로", 37.556000, 126.936000);       // ❌   서울
        createPlace(1006, "숙대입구역", "서울 용산구 청파로", 37.542000, 126.975000);     // ❌   서울
        createPlace(1007, "강남역", "서울 강남구 강남대로", 37.498000, 127.028000);       // ❌   서울

        // When & Then : 의정부역 기준으로 1KM 반경 내 장소 조회 시 예외 테스트
        MapException exception = Assertions.assertThrows(MapException.class, () -> {
            mapService.getPlacesByCoordinate(37.738569, 127.045147, 1);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BY_COORDINATE_NOT_FOUND_PLACE_LIST);
    }


    @Test
    void 마커_정보_조회_실패_PLACE_NOT_FOUND() {

        // Given & When & Then
        MapException exception = Assertions.assertThrows(MapException.class, () -> {
            mapService.getPlaceInfo(1);
        });
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PLACE_NOT_FOUND);
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
    void createFavoriteGroup(long userId, String groupName) {
        GroupCreateRequest request = GroupCreateRequest.builder().groupName(groupName).build();
        groupService.createGroup(request, userId);
    }

    // 그룹에 즐겨찾기 장소 추가
    void addFavoriteGroup(
            long kakaoPlaceId,
            String address,
            String placeName,
            String memo,
            double lat, double lng,
            long groupId, long userId) {
        // 해당 그룹에 즐겨찾기 장소 추가
        FavoriteAddRequest request = FavoriteAddRequest.builder()
                .kakaoPlaceId(kakaoPlaceId)
                .address(address)
                .placeName(placeName)
                .lat(lat)
                .lng(lng)
                .memo(memo)
                .build();

        favoriteService.addFavorite(groupId, request, userId);
    }

    public static double calculateHaversine(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371; // 지구 반지름 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

}
