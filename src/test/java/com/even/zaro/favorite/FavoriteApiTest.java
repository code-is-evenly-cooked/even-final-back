package com.even.zaro.favorite;

import com.even.zaro.dto.favorite.FavoriteAddRequest;
import com.even.zaro.dto.favorite.FavoriteEditRequest;
import com.even.zaro.dto.favorite.FavoriteResponse;
import com.even.zaro.dto.group.GroupCreateRequest;
import com.even.zaro.dto.group.GroupResponse;
import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.favorite.FavoriteException;
import com.even.zaro.global.exception.map.MapException;
import com.even.zaro.repository.FavoriteGroupRepository;
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
import java.util.stream.Collectors;

import static com.even.zaro.entity.QUser.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @Autowired
    private FavoriteGroupRepository favoriteGroupRepository;

    @Test
    void 그룹에_즐겨찾기_추가_성공_테스트() {

        // Given
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");
        createFavoriteGroup(user.getId(), "서울 맛집");

            // 그룹 리스트를 조회하고 첫번째 그룹의 id를 저장
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId());
        long firstGroupId = favoriteGroups.getFirst().getId();

            // 예시 장소 1개 추가
        createPlace(1, "이자카야 하나", "서울특별시 중구 을지로 100", 36.21, 53.21);

        List<Long> placeIdList = placeRepository.findAll().stream()
                .map(Place::getId).toList();

        // When
            // 예시 장소 그룹에 즐겨찾기 추가
        addFavoriteGroup(placeIdList.getFirst(), firstGroupId, "친구랑 가고 싶은 감성카페", user.getId());

        // Then
        Place place = placeRepository.findById(placeIdList.getFirst())
                .orElseThrow(() -> new MapException(ErrorCode.PLACE_NOT_FOUND));

        assertThat(place.getName()).isEqualTo("이자카야 하나");
        assertThat(place.getAddress()).isEqualTo("서울특별시 중구 을지로 100");
    }

    @Test
    void 그룹의_즐겨찾기_리스트_조회_성공_테스트() {

        // Given
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");
        createFavoriteGroup(user.getId(), "서울 맛집");

            // 그룹 리스트를 조회하고 첫번째 그룹의 id를 저장
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId());
        long firstGroupId = favoriteGroups.getFirst().getId();

            // 예시 장소 3개 추가
        createPlace(1, "이자카야 하나", "서울특별시 중구 을지로 100", 36.21, 53.21);
        createPlace(2, "카페 드롭탑", "서울특별시 종로구 종로 1길 1", 313.21, 533.21);
        createPlace(3, "삼겹살 맛집", "서울특별시 중구 퇴계로 200", 36123.21, 12.21);

        List<Long> placeIdList = placeRepository.findAll().stream()
                .map(Place::getId).toList();

        // When
            // 예시 장소 3개 그룹에 즐겨찾기 추가
        addFavoriteGroup(placeIdList.getFirst(), firstGroupId, "친구랑 가고 싶은 감성카페", user.getId());
        addFavoriteGroup(placeIdList.get(1), firstGroupId, "드롭다운 예예", user.getId());
        addFavoriteGroup(placeIdList.getLast(), firstGroupId, "삼겹살은 여기로", user.getId());

        // Then
            // 그룹의 즐겨찾기 리스트 조회
        List<FavoriteResponse> groupItems = favoriteService.getGroupItems(firstGroupId);

        assertThat(placeIdList).hasSize(3); // 현재 DB에 저장된 장소의 개수 검증
        assertThat(groupItems.size()).isEqualTo(3); // 개수 검증
        assertThat(groupItems.stream().map(FavoriteResponse::getMemo) // 그룹의 즐겨찾기 이름 리스트 검증
                .toList()).containsExactlyInAnyOrder("드롭다운 예예", "삼겹살은 여기로", "친구랑 가고 싶은 감성카페");
    }

    @Test
    void 즐겨찾기_메모_수정_성공_테스트() {
        // Given : user 객체와 그룹 생성
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");
        createFavoriteGroup(user.getId(), "서울 맛집");

            // 그룹 리스트를 조회하고 첫번째 그룹의 id를 저장
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId());
        long firstGroupId = favoriteGroups.getFirst().getId();

            // 예시 장소 1개 추가
        createPlace(1, "이자카야 하나", "서울특별시 중구 을지로 100", 36.21, 53.21);

        List<Long> placeIdList = placeRepository.findAll().stream()
                .map(Place::getId).toList();

            // 예시 장소 그룹에 즐겨찾기 추가
        addFavoriteGroup(placeIdList.getFirst(), firstGroupId, "친구랑 가고 싶은 감성카페", user.getId());

        // When : 즐겨찾기의 메모를 수정
        editFavoriteGroup(placeIdList.getFirst(), "친구랑 절대 가기 싫은 카페", user.getId());

        // Then : 해당 즐겨찾기의 메모가 수정한 메모 텍스트와 일치하는지 확인
        Favorite favorite = favoriteRepository.findById(placeIdList.getFirst())
                .orElseThrow(() -> new MapException(ErrorCode.FAVORITE_NOT_FOUND));

        assertThat(favorite.getMemo()).isEqualTo("친구랑 절대 가기 싫은 카페");
    }

    @Test
    void 즐겨찾기_삭제_성공_테스트() {
        // Given : user 객체와 그룹 생성
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");
        createFavoriteGroup(user.getId(), "서울 맛집");

            // 그룹 리스트를 조회하고 첫번째 그룹의 id를 저장
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId());
        long firstGroupId = favoriteGroups.getFirst().getId();

            // 예시 장소 1개 추가
        createPlace(1, "이자카야 하나", "서울특별시 중구 을지로 100", 36.21, 53.21);

        List<Long> placeIdList = placeRepository.findAll().stream()
                .map(Place::getId).toList();

            // 예시 장소 그룹에 즐겨찾기 추가
        addFavoriteGroup(placeIdList.getFirst(), firstGroupId, "친구랑 가고 싶은 감성카페", user.getId());

        // When : 즐겨찾기를 삭제
        favoriteService.deleteFavorite(placeIdList.getFirst(), user.getId());

        // Then : 해당 즐겨찾기의 메모가 수정한 메모 텍스트와 일치하는지 확인
        Favorite favorite = favoriteRepository.findById(placeIdList.getFirst())
                .orElseThrow(() -> new MapException(ErrorCode.FAVORITE_NOT_FOUND));

        assertThat(favorite.isDeleted()).isEqualTo(true);
    }

    @Test
    void 이미_존재하는_즐겨찾기_추가_시도_예외_FAVORITE_ALREADY_EXISTS() {
        // Given : user 객체와 그룹 생성
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");
        createFavoriteGroup(user.getId(), "서울 맛집");

            // 그룹 리스트를 조회하고 첫번째 그룹의 id를 저장
        List<GroupResponse> favoriteGroups = groupService.getFavoriteGroups(user.getId());
        long firstGroupId = favoriteGroups.getFirst().getId();

            // 예시 장소 1개 추가
        createPlace(1, "이자카야 하나", "서울특별시 중구 을지로 100", 36.21, 53.21);

        List<Long> placeIdList = placeRepository.findAll().stream()
                .map(Place::getId).toList();

            // 예시 장소 그룹에 즐겨찾기 추가
        addFavoriteGroup(placeIdList.getFirst(), firstGroupId, "친구랑 가고 싶은 감성카페", user.getId());


        // When & Then : 같은 placeId 추가 시도
        FavoriteException exception = assertThrows(FavoriteException.class, () -> {
            addFavoriteGroup(placeIdList.getFirst(), firstGroupId, "친구랑 가고 싶은 감성카페", user.getId());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FAVORITE_ALREADY_EXISTS);
    }


    @Test
    void 존재하지_않는_즐겨찾기_삭제_시도_FAVORITE_NOT_FOUND() {
        // Given : user 객체와 그룹 생성
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");

        // WHen & Then : 즐겨찾기를 추가하지 않은 상태에서 삭제 시도
        FavoriteException exception = assertThrows(FavoriteException.class, () -> {
            favoriteService.deleteFavorite(0, user.getId());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FAVORITE_NOT_FOUND);
    }

    @Test
    void 존재하지_않는_즐겨찾기_메모_수정_시도_FAVORITE_NOT_FOUND() {
        // Given : user 객체와 그룹 생성
        User user = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");

        // WHen & Then : 즐겨찾기를 추가하지 않은 상태에서 삭제 시도
        FavoriteException exception = assertThrows(FavoriteException.class, () -> {
            editFavoriteGroup(0, "존재하지 않는 메모를 수정해볼까요", user.getId());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FAVORITE_NOT_FOUND);
    }

    @Test
    void 다른_유저의_즐겨찾기_메모_수정_시도_UNAUTHORIZED_FAVORITE_UPDATE() {
        // Given : user 객체와 그룹 생성
        User user1 = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");
        User user2 = createUser("tlaehdgns@naver.com", "Test1234!", "자취왕");

            // 예시 장소 1개 추가
        createPlace(1, "이자카야 하나", "서울특별시 중구 을지로 100", 36.21, 53.21);

        List<Long> placeIdList = placeRepository.findAll().stream().map(Place::getId).toList();

            // 그룹 추가
        createFavoriteGroup(user1.getId(), "서울 맛집");

        List<Long> GroupIdList = favoriteGroupRepository.findAll().stream().map(FavoriteGroup::getId).toList();

            // 그룹에 즐겨찾기 추가
        addFavoriteGroup(placeIdList.getFirst(), GroupIdList.getFirst(), "이자카야 맛집", user1.getId());

        // When & Then : 다른 유저의 즐겨찾기 메모 수정 시도
        FavoriteException exception = assertThrows(FavoriteException.class, () -> {
            editFavoriteGroup(placeIdList.getFirst(), "다른 유저의 즐겨찾기 메모 수정 시도", user2.getId());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_FAVORITE_UPDATE);
    }

    @Test
    void 다른_유저의_즐겨찾기_삭제_시도_UNAUTHORIZED_FAVORITE_DELETE() {
        // Given : user 객체와 그룹 생성
        User user1 = createUser("ehdgnstla@naver.com", "Test1234!", "동훈");
        User user2 = createUser("tlaehdgns@naver.com", "Test1234!", "자취왕");

        // 예시 장소 1개 추가
        createPlace(1, "이자카야 하나", "서울특별시 중구 을지로 100", 36.21, 53.21);

        List<Long> placeIdList = placeRepository.findAll().stream().map(Place::getId).toList();

        // 그룹 추가
        createFavoriteGroup(user1.getId(), "서울 맛집");

        List<Long> GroupIdList = favoriteGroupRepository.findAll().stream().map(FavoriteGroup::getId).toList();

        // 그룹에 즐겨찾기 추가
        addFavoriteGroup(placeIdList.getFirst(), GroupIdList.getFirst(), "이자카야 맛집", user1.getId());

        // When & Then : 다른 유저의 즐겨찾기 삭제 시도
        FavoriteException exception = assertThrows(FavoriteException.class, () -> {
            favoriteService.deleteFavorite(placeIdList.getFirst(), user2.getId());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_FAVORITE_DELETE);
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
    void createFavoriteGroup(long userId, String groupName) {
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
