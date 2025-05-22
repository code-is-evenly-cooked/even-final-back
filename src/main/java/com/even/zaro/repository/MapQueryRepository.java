package com.even.zaro.repository;

import com.even.zaro.entity.Place;
import com.even.zaro.entity.QPlace;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@RequiredArgsConstructor
@Repository
public class MapQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 주어진 위도/경도 기준으로 반경 distanceKm 이내의 장소를 조회합니다.
     * 사각형 모양으로 조회 (사용자 중심으로 원형 조회 XXXXXXX)
     *
     * @param latitude 사용자의 현재 위도 (예: 37.5665)
     * @param longitude 사용자의 현재 경도 (예: 126.9780)
     * @param distanceKm 검색 반경 (단위: km, 예: 1.0 = 1km, 0.5 = 500m)
     * @return 반경 내의 Place 목록
     */
    public List<Place> findPlaceByCoordinate(double latitude, double longitude, double distanceKm) {
        QPlace place = QPlace.place;

        double latDelta = distanceKm / 111.0; // 경도 계산
        double lngDelta = distanceKm / (111.0 * Math.cos(Math.toRadians(latitude))); // 위도 계산

        NumberExpression<Double> minLat = Expressions.numberTemplate(Double.class, "{0} - {1}", latitude, latDelta);
        NumberExpression<Double> maxLat = Expressions.numberTemplate(Double.class, "{0} + {1}", latitude, latDelta);

        NumberExpression<Double> minLng = Expressions.numberTemplate(Double.class, "{0} - {1}", longitude, lngDelta);
        NumberExpression<Double> maxLng = Expressions.numberTemplate(Double.class, "{0} + {1}", longitude, lngDelta);


        // 원형으로 distance기준으로 조회될 수 있도록 정밀 거리필터 추가
        NumberExpression<Double> haversineDistance = Expressions.numberTemplate(
                Double.class,
                "6371 * acos(least(1.0, cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1}))))",
                Expressions.constant(latitude),
                place.lat,
                place.lng,
                Expressions.constant(longitude)
        );

        return jpaQueryFactory
                .select(place)
                .from(place)
                .where(
                        place.lat.between(minLat, maxLat), // 계산된 경도 범위
                        place.lng.between(minLng, maxLng), // 계산된 위도 범위
                        haversineDistance.loe(distanceKm)
                ).fetch();
    }
}