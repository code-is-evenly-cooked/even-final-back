package com.even.zaro.repository;

import com.even.zaro.entity.Post;
import com.even.zaro.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserAndIsDeletedFalse(User user, Pageable pageable);

    int countByUserAndIsDeletedFalse(User user);

    Page<Post> findByCategoryAndIsDeletedFalse(Post.Category category, Pageable pageable);

    Page<Post> findByIsDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"imageUrlList"})
    Optional<Post> findByIdAndIsDeletedFalse(Long postId);

    List<Post> findTop5ByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(Post.Category category);

    boolean existsByIdAndIsDeletedFalse(Long postId);
}
