package com.even.zaro.repository;

import com.even.zaro.entity.Favorite;
import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.Place;
import com.even.zaro.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @EntityGraph(attributePaths = { "user"})
    List<Favorite> findAllByPlace(Place place);
    List<Favorite> findAllByGroupAndDeletedFalse(FavoriteGroup group);

    boolean existsByPlaceAndUser(Place place, User user);

    List<Favorite> findAllByGroup(FavoriteGroup group);
}
