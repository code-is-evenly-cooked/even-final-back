package com.even.zaro.entity;

import com.even.zaro.listener.FollowListener;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "follow", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"follower_id", "followee_id"})
        // 고유 제약 조건(컬럼 조합 unique) : 같은 사용자가 동일한 사용자를 중복으로 팔로우할 수 없도록 설정
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(FollowListener.class)
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower; // 팔로우 하는 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee; // 팔로우 당하는 사용자

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
