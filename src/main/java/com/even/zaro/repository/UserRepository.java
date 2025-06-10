package com.even.zaro.repository;

import com.even.zaro.entity.Provider;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

    boolean existsByNickname(String nickname);

    Optional<User> findByNickname(String nickname);

    List<User> findByStatusAndLastLoginAtBefore(Status status, LocalDateTime time);
    List<User> findByStatusAndUpdatedAtBefore(Status status, LocalDateTime time);
    List<User> findByStatusAndDeletedAtBefore(Status status, LocalDateTime threshold);

    @Query("""
            SELECT u FROM User u
            WHERE u.status = :status
            AND u.lastLoginAt < :time
            AND u.id NOT IN (
                SELECT l.userId FROM DormancyNoticeLog l
            )
            """)
    List<User> findDormancyNoticeTargets(Status status, LocalDateTime time);
}
