package com.even.zaro.repository;

import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndNickname(String email, String nickname);

    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

    boolean existsByNickname(String nickname);

    Optional<User> findByNickname(String nickname);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM User u WHERE u.status = :status AND u.createdAt < :threshold")
    int deleteByStatusAndCreatedAtBefore(@Param("status") Status status, @Param("threshold") LocalDateTime threshold);
    List<User> findByStatusAndLastLoginAtBefore(Status status, LocalDateTime time);
    List<User> findByStatusAndUpdatedAtBefore(Status status, LocalDateTime time);
    List<User> findByStatusAndDeletedAtBefore(Status status, LocalDateTime threshold);

    @Query(value = """
            SELECT * FROM users u
            WHERE u.status = :status
            AND u.last_login_at < :time
            AND NOT EXISTS (
                SELECT l.user_id FROM dormancy_notice_log l
                WHERE l.user_id = u.id
                AND l.last_checked_login_date = DATE(u.last_login_at)
            )
            """, nativeQuery = true)
    List<User> findDormancyNoticeTargetsNative(@Param("status") Status status, @Param("time") LocalDateTime time);
}
