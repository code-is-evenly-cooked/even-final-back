package com.even.zaro.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "dormancy_notice_log")
public class DormancyNoticeLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "last_checked_login_date")
    private LocalDate lastCheckedLoginDate;

    @Column(name = "sent_at")
    private LocalDateTime sentAt = LocalDateTime.now();

    public DormancyNoticeLog(Long userId, LocalDate baseDate) {
        this.userId = userId;
        this.lastCheckedLoginDate = baseDate;
    }
}
