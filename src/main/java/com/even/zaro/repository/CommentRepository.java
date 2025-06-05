package com.even.zaro.repository;

import com.even.zaro.entity.Comment;
import com.even.zaro.entity.Post;
import com.even.zaro.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByUserAndIsDeletedFalse(User user, Pageable pageable);

    Page<Comment> findByPostIdAndIsDeletedFalse(Long postId, Pageable pageable);

    Optional<Comment> findByIdAndIsDeletedFalse(Long commentId);

    int countByPostAndIsDeletedFalse(Post post);
}