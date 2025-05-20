package com.even.zaro.repository;

import com.even.zaro.entity.Favorite;
import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.Place;
import com.even.zaro.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByGroup(FavoriteGroup group);

    @EntityGraph(attributePaths = { "user"})
    List<Favorite> findAllByPlace(Place place);
    List<Favorite> findByPlaceAndUser(Place place, User user);
}
