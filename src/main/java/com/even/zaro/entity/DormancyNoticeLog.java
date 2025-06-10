package com.even.zaro.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "dormancy_notice_log")
public class DormancyNoticeLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDateTime sentAt = LocalDateTime.now();

    public DormancyNoticeLog(Long userId) {
        this.userId = userId;
    }
}
