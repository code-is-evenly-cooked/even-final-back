package com.even.zaro.repository;

import com.even.zaro.entity.Post;
import com.even.zaro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserAndIsDeletedFalse(User user);
}
