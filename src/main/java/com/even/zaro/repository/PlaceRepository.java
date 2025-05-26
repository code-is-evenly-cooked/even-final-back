package com.even.zaro.repository;

import com.even.zaro.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Place findByKakaoPlaceId(long kakaoPlaceId);
}
