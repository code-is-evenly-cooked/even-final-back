package com.even.zaro.favorite;

import com.even.zaro.dto.favorite.FavoriteAddRequest;
import com.even.zaro.dto.favorite.FavoriteEditRequest;
import com.even.zaro.dto.favorite.FavoriteResponse;
import com.even.zaro.dto.group.GroupCreateRequest;
import com.even.zaro.dto.group.GroupResponse;
import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.map.MapException;
import com.even.zaro.repository.FavoriteRepository;
import com.even.zaro.repository.PlaceRepository;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.FavoriteService;
import com.even.zaro.service.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class FavoriteApiTest {

    @Autowired
    FavoriteService favoriteService;

    @Autowired
    GroupService groupService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Test
    void 그룹에_즐겨찾기_추가_성공_테스트() {

        // Given
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");
        craeteFavoriteGroup(user.getId(), "서울 맛집");

            // 그룹 리스트를 조회하고 첫번째 그룹의 id를 저장
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId());
        long firstGroupId = favoriteGroups.getFirst().getId();

            // 예시 장소 1개 추가
        createPlace(1, "이자카야 하나", "서울특별시 중구 을지로 100", 36.21, 53.21);

        List<Long> all = placeRepository.findAll().stream()
                .map(Place::getId).toList();

        // When
            // 예시 장소 그룹에 즐겨찾기 추가
        addFavoriteGroup(all.getFirst(), firstGroupId, "친구랑 가고 싶은 감성카페", user.getId());

        // Then
        Place place = placeRepository.findById(all.getFirst())
                .orElseThrow(() -> new MapException(ErrorCode.PLACE_NOT_FOUND));

        assertThat(place.getName()).isEqualTo("이자카야 하나");
        assertThat(place.getAddress()).isEqualTo("서울특별시 중구 을지로 100");
    }

    @Test
    void 그룹의_즐겨찾기_리스트_조회_성공_테스트() {

        // Given
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");
        craeteFavoriteGroup(user.getId(), "서울 맛집");

            // 그룹 리스트를 조회하고 첫번째 그룹의 id를 저장
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId());
        long firstGroupId = favoriteGroups.getFirst().getId();

            // 예시 장소 3개 추가
        createPlace(1, "이자카야 하나", "서울특별시 중구 을지로 100", 36.21, 53.21);
        createPlace(2, "카페 드롭탑", "서울특별시 종로구 종로 1길 1", 313.21, 533.21);
        createPlace(3, "삼겹살 맛집", "서울특별시 중구 퇴계로 200", 36123.21, 12.21);

        List<Long> all = placeRepository.findAll().stream()
                .map(Place::getId).toList();

        // When
            // 예시 장소 3개 그룹에 즐겨찾기 추가
        addFavoriteGroup(all.getFirst(), firstGroupId, "친구랑 가고 싶은 감성카페", user.getId());
        addFavoriteGroup(all.get(1), firstGroupId, "드롭다운 예예", user.getId());
        addFavoriteGroup(all.getLast(), firstGroupId, "삼겹살은 여기로", user.getId());

        // Then
            // 그룹의 즐겨찾기 리스트 조회
        List<FavoriteResponse> groupItems = favoriteService.getGroupItems(firstGroupId);

        assertThat(all).hasSize(3); // 현재 DB에 저장된 장소의 개수 검증
        assertThat(groupItems.size()).isEqualTo(3); // 개수 검증
        assertThat(groupItems.stream().map(FavoriteResponse::getMemo) // 그룹의 즐겨찾기 이름 리스트 검증
                .toList()).containsExactlyInAnyOrder("드롭다운 예예", "삼겹살은 여기로", "친구랑 가고 싶은 감성카페");
    }

    @Test
    void 즐겨찾기_메모_수정_성공_테스트() {
        // Given
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");
        craeteFavoriteGroup(user.getId(), "서울 맛집");

            // 그룹 리스트를 조회하고 첫번째 그룹의 id를 저장
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId());
        long firstGroupId = favoriteGroups.getFirst().getId();

            // 예시 장소 1개 추가
        createPlace(1, "이자카야 하나", "서울특별시 중구 을지로 100", 36.21, 53.21);

        List<Long> all = placeRepository.findAll().stream()
                .map(Place::getId).toList();

            // 예시 장소 그룹에 즐겨찾기 추가
        addFavoriteGroup(all.getFirst(), firstGroupId, "친구랑 가고 싶은 감성카페", user.getId());

        // When
        editFavoriteGroup(all.getFirst(), "친구랑 절대 가기 싫은 카페", user.getId());

        // Then
        Favorite favorite = favoriteRepository.findById(all.getFirst())
                .orElseThrow(() -> new MapException(ErrorCode.FAVORITE_NOT_FOUND));

        assertThat(favorite.getMemo()).isEqualTo("친구랑 절대 가기 싫은 카페");
    }

    @Test
    void 즐겨찾기_삭제_성공_테스트() {

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
    void createPlace(long kakaoPlaceId, String name, String address, double lat, double lng) {
        placeRepository.save(Place.builder()
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

    // 즐겨찾기 메모 수정
    void editFavoriteGroup(long placeId, String memo, long userId) {
        FavoriteEditRequest editRequest = FavoriteEditRequest.builder().memo(memo).build();
        favoriteService.editFavoriteMemo(placeId, editRequest, userId);
    }

}
