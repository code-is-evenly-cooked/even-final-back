package com.even.zaro.repository;

import com.even.zaro.entity.Favorite;
import com.even.zaro.entity.FavoriteGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByGroup(FavoriteGroup group);
}
