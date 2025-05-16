package com.even.zaro.repository;

import com.even.zaro.entity.Follow;
import com.even.zaro.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowee(User follower, User followee);
}
