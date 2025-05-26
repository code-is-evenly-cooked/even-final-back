package com.even.zaro.service;

import com.even.zaro.dto.post.ReportRequestDTO;
import com.even.zaro.entity.Post;
import com.even.zaro.entity.PostReport;
import com.even.zaro.entity.ReportReasonType;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.post.PostException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.PostReportRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostReportService {

    private final PostReportRepository postReportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void reportPost(Long postId, ReportRequestDTO request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        if (post.getUser().getId().equals(userId)) {
            throw new PostException(ErrorCode.CANNOT_REPORT_OWN_POST);
        }

        if (request.reasonType() == ReportReasonType.ETC &&
                (request.reasonText() == null || request.reasonText().trim().isEmpty())) {
            throw new PostException(ErrorCode.REASON_TEXT_REQUIRED_FOR_ETC);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        if (postReportRepository.existsByPostAndUser(post, user)) {
            throw new PostException(ErrorCode.ALREADY_REPORTED_POST);
        }

        postReportRepository.save(PostReport.builder()
                .post(post)
                .user(user)
                .reasonType(request.reasonType())
                .reasonText(request.reasonText())
                .createdAt(LocalDateTime.now())
                .build());

        int reportCount = postReportRepository.countByPost(post);
        post.changeReportCount(reportCount);

        if (reportCount >= 5 && !post.isReported()){
            post.markAsReported();
        }
    }
}
