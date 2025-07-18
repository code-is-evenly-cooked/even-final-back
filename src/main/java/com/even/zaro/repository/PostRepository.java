package com.even.zaro.repository;

import com.even.zaro.entity.Post;
import com.even.zaro.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserAndIsDeletedFalse(User user, Pageable pageable);

    int countByUserAndIsDeletedFalse(User user);

    Page<Post> findByCategoryAndIsDeletedFalseAndIsReportedFalse(Post.Category category, Pageable pageable);

    Page<Post> findByIsDeletedFalseAndIsReportedFalse(Pageable pageable);

    Optional<Post> findByIdAndIsDeletedFalseAndIsReportedFalse(Long postId);

    @EntityGraph(attributePaths = {"postImageList"})
    Optional<Post> findByIdAndIsDeletedFalse(Long postId);

    List<Post> findTop5ByCategoryAndIsDeletedFalseAndIsReportedFalseOrderByCreatedAtDesc(Post.Category category);

    boolean existsByIdAndIsDeletedFalseAndIsReportedFalse(Long postId);

    Page<Post> findByCategoryAndTagAndIsDeletedFalseAndIsReportedFalse(Post.Category category, Post.Tag tag, Pageable pageable);

    @Query("""
    SELECT p FROM Post p
    WHERE p.isDeleted = false
      AND p.isReported = false
      AND p.score > :minScore
    ORDER BY p.score DESC, p.createdAt DESC
""")
    List<Post> findTopPosts(@Param("minScore") int minScore, Pageable pageable);
}
