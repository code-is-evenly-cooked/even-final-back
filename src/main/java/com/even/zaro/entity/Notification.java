package com.even.zaro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@Builder(toBuilder = true) // testCode용으로 추가 (실제는 Listener에서 자동생성)
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum Type {
        LIKE,
        COMMENT,
        FOLLOW
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
