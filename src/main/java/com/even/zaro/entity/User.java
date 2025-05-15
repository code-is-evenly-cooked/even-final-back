package com.even.zaro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = true) // 소셜 로그인 시 비워둘 수 있음
    private String password;

    @Column(name = "profile_image", nullable = false) // 디폴트 이미지 필요
    private String profileImage;

    @Column(name = "birth_date", nullable = true)
    private LocalDate birthDate;

    @Column(name = "live_alone_date", nullable = true)
    private LocalDate liveAloneDate;

    @Column(nullable = true, length = 10)
    private String gender;

    @Column(nullable = true, length = 4)
    private String mbti;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at", nullable = true)
    private LocalDateTime lastLoginAt;

    @Column(name = "follower_count", nullable = false)
    private int followerCount = 0;

    @Column(name = "following_count", nullable = false)
    private int followingCount = 0;


    public enum Status {
        PENDING, ACTIVE, DORMANT, DELETED
    }
}