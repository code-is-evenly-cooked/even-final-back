package com.even.zaro.service;

import com.even.zaro.dto.post.ReportRequestDTO;
import com.even.zaro.dto.post.ReportResponseDto;
import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.post.PostException;
import com.even.zaro.repository.PostReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostReportService {

    private final PostReportRepository postReportRepository;
    private final UserService userService;
    private final PostService postService;

    @Transactional
    public ReportResponseDto reportPost(Long postId, ReportRequestDTO request, Long userId) {
        Post post = postService.findPostOrThrow(postId);

        User user = userService.findUserById(userId);
        userService.validateActiveUser(user);

        post.validateNotOwner(user);
        request.validateReasonTextOrThrow();

        if (postReportRepository.existsByPostAndUser(post, user)) {
            throw new PostException(ErrorCode.ALREADY_REPORTED_POST);
        }

        postReportRepository.save(PostReport.builder()
                .post(post)
                .user(user)
                .reasonType(request.getReasonType())
                .reasonText(request.getReasonText())
                .createdAt(LocalDateTime.now())
                .build());

        int reportCount = postReportRepository.countByPost(post);
        post.changeReportCount(reportCount);

        if (reportCount >= 5 && !post.isReported()){
            post.markAsReported();
        }
        return new ReportResponseDto(
                request.getReasonType(),
                request.getReasonType().getDescription(),
                request.getReasonText()
        );
    }
}
