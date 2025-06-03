package com.even.zaro.entity;

import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.post.PostException;
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

    @Column(nullable = false,columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tag tag;

    @ElementCollection
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "post_images")
    @Builder.Default
    private List<String> postImageList = new ArrayList<>();

    @Column(name = "thumbnail_image")
    private String thumbnailImage;

    @Column(name = "is_reported", nullable = false)
    @Builder.Default
    private boolean isReported = false;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    @Builder.Default
    private int commentCount = 0;

    @Column(name = "report_count", nullable = false)
    @Builder.Default
    private int reportCount = 0;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "score", nullable = false)
    @Builder.Default
    private double score = 0.0;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeTag(Tag tag) {
        this.tag = tag;
    }

    public void changeImageList(List<String> postImageList) {
        this.postImageList = postImageList;
    }

    public void changeThumbnail(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public void changeLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void changeReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public void markAsReported() {
        this.isReported = true;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void changeCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void updateScore(){
        this.score = (this.likeCount * 3.0) + (this.commentCount * 5.0);
    }

    public enum Category {
        TOGETHER(List.of(Tag.GROUP_BUY, Tag.SHARING, Tag.EXCHANGE)),   //같이쓰자
        DAILY_LIFE(List.of(Tag.TIPS, Tag.QUESTIONS)), //자취일상
        RANDOM_BUY(List.of(Tag.TREASURE, Tag.REGRET));//텅장일기

        private final List<Tag> allowedTags;

        Category(List<Tag> allowedTags) {
            this.allowedTags = allowedTags;
        }

        public boolean isAllowed(Tag tag) {
            return allowedTags.contains(tag);
        }
    }

    public enum Tag {

        // 자취일상 - 자취꿀팁, 질문있어요
        TIPS, QUESTIONS,

        // 같이쓰기 - 같이사기, 나눔해요, 물물교환
        GROUP_BUY, SHARING, EXCHANGE,

        // 텅장일기 - 소중한 꿀템, 후회막심
        TREASURE, REGRET
    }

    public void validateNotOwner(User user) {
        if (this.user.equals(user)) {
            throw new PostException(ErrorCode.CANNOT_REPORT_OWN_POST);
        }
    }

}
