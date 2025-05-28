package com.even.zaro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment_report", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"comment_id","user_id"})
})

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReasonType reasonType;

    private String reasonText;

    private LocalDateTime createdAt;
}
