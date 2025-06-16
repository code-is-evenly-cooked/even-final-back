package com.even.zaro.repository;

import com.even.zaro.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByKakaoPlaceId(long kakaoPlaceId);
}
