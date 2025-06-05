package com.even.zaro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "favorite_group")
public class FavoriteGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Group 이름
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "favorite_count", nullable = false) // 그룹에 속한 즐겨찾기 개수
    @Builder.Default
    private int favoriteCount = 0;

    public void setIsDeleted() {
        this.isDeleted = true;
    }

    public void editGroupName(String name) {
        this.name = name;
    }

    public void incrementFavoriteCount() {
        this.favoriteCount++;
    }

    public void decrementFavoriteCount() {
        this.favoriteCount--;
    }


}
