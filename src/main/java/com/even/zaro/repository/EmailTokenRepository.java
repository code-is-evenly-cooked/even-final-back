package com.even.zaro.repository;

import com.even.zaro.entity.EmailToken;
import com.even.zaro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    Optional<EmailToken> findByToken(String token);

    void deleteByUser(User user);
}