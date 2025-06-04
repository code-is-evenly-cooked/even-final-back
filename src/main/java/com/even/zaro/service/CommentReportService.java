package com.even.zaro.service;

import com.even.zaro.dto.post.ReportRequestDTO;
import com.even.zaro.dto.post.ReportResponseDto;
import com.even.zaro.entity.*;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.CustomException;
import com.even.zaro.global.exception.comment.CommentException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.CommentReportRepository;
import com.even.zaro.repository.CommentRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final CommentReportRepository commentReportRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public ReportResponseDto reportComment(Long commentId, ReportRequestDTO request, Long userId) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.getUser().getId().equals(userId)) {
            throw new CommentException(ErrorCode.CANNOT_REPORT_OWN_COMMENT);
        }

        request.validateReasonTextOrThrow();

       User user = userService.findUserById(userId);
        userService.validateNotPending(user);

        if (commentReportRepository.existsByCommentAndUser(comment, user)) {
            throw new CommentException(ErrorCode.ALREADY_REPORTED_COMMENT);
        }

        commentReportRepository.save(CommentReport.builder()
                .comment(comment)
                .user(user)
                .reasonType(request.getReasonType())
                .reasonText(request.getReasonText())
                .createdAt(LocalDateTime.now())
                .build());


        int reportCount = commentReportRepository.countByComment(comment);
        comment.changeReportCount(reportCount);

        if (reportCount >=5 && !comment.isReported()) {
            comment.markAsReported();
        }

        return new ReportResponseDto(
                request.getReasonType(),
                request.getReasonType().getDescription(),
                request.getReasonText()
        );
    }
}
