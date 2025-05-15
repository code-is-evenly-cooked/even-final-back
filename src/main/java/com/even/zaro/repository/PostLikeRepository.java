package com.even.zaro.repository;

import com.even.zaro.entity.PostLike;
import com.even.zaro.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Page<PostLike> findByUser(User user, Pageable pageable);
}