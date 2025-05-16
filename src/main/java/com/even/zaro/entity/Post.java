package com.even.zaro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostTag tag;

    @ElementCollection
    @CollectionTable(name = "post_image", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> imageUrlList = new ArrayList<>();

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "is_reported")
    private boolean isReported = false;
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "like_count")
    private int likeCount = 0;
    @Column(name = "comment_count")
    private int commentCount = 0;
    @Column(name = "report_count")
    private int reportCount = 0;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void increaseReportCount() {
        this.reportCount++;
        if(this.reportCount >= 5 ){
            this.isReported = true;
        }
    }

    public void update(String title, String content, PostTag newTag, List<String> newImages, String newThumbnailUrl) {
        this.title = title;
        this.content = content;
        this.tag = newTag;
        this.imageUrlList = newImages;
        this.thumbnailUrl = newThumbnailUrl;

    }

    public void softDelete() {
        this.isDeleted = true;
    }

}
