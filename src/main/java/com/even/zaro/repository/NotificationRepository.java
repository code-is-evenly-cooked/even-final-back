package com.even.zaro.repository;

import com.even.zaro.entity.Notification;
import com.even.zaro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);

    Optional<Notification> findByIdAndUserId(Long notificationId, Long userId);

    List<Notification> findAllByUserIdAndIsReadFalse(Long userId);

    void deleteByCreatedAtBefore(LocalDateTime deletingDate);
}
