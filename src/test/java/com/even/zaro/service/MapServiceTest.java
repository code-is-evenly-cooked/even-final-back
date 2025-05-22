package com.even.zaro.service;

import com.even.zaro.entity.Place;
import com.even.zaro.repository.FavoriteRepository;
import com.even.zaro.repository.MapQueryRepository;
import com.even.zaro.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

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


    @DisplayName("장소 정보 조회 성공 테스트")
    @Test
    void 장소_정보_조회_성공_테스트() {
        //given


//        when(userRepository.existsByNickname("이브니")).thenReturn(true);
//
//        //when
//        SignUpRequestDto requestDto = new SignUpRequestDto("test2@even.com", "Test1234!", "이브니");
//
//        //then
//        UserException userException = assertThrows(UserException.class, () -> authService.signUp(requestDto));
//        assertEquals(ErrorCode.NICKNAME_ALREADY_EXISTED, userException.getErrorCode());
    }


    // 임의의 장소를 생성하는 메서드
    void createPlace(long kakaoplaceId, String name, double lat, double lng, String address) {
        placeRepository.save(Place.builder()
                .kakaoPlaceId(1)
                .name(name)
                .lat(lat)
                .lng(lng)
                .address(address)
                .build());
    }
}
