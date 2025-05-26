package com.even.zaro.repository;

import com.even.zaro.entity.Post;
import com.even.zaro.entity.PostReport;
import com.even.zaro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    boolean existsByPostAndUser(Post post, User user);
    int countByPost(Post post);
}
