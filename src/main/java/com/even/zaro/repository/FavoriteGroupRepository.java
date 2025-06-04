package com.even.zaro.repository;

import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteGroupRepository extends JpaRepository<FavoriteGroup, Long> {
    List<FavoriteGroup> findByUser(User user);

    boolean existsByUserIdAndName(Long userId, String name);
}
