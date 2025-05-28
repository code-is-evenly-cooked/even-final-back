package com.even.zaro.repository;

import com.even.zaro.entity.Comment;
import com.even.zaro.entity.CommentReport;
import com.even.zaro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    boolean existsByCommentAndUser(Comment comment, User user);
    int countByComment(Comment comment);
}
