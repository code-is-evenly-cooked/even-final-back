package com.even.zaro.repository;

import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteGroupRepository extends JpaRepository<FavoriteGroup, Long> {
    boolean existsByUserIdAndName(Long userId, String name);

    List<FavoriteGroup> findAllByUserAndDeletedFalse(User user);
}
