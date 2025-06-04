package com.even.zaro.entity;

import com.even.zaro.listener.CommentListener;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(CommentListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(name = "is_reported", nullable = false)
    @Builder.Default
    private boolean isReported = false;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentioned_user_id")
    private User mentionedUser;

    @Column(name = "report_count", nullable = false)
    @Builder.Default
    private int reportCount = 0;

    public void updateContent(String content) {
        this.content = content;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public void changeReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public void markAsReported() {
        this.isReported = true;
    }
}