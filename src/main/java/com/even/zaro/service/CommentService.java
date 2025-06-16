package com.even.zaro.service;

import com.even.zaro.dto.comment.CommentPageResponse;
import com.even.zaro.dto.comment.CommentResponseDto;
import com.even.zaro.dto.comment.CommentRequestDto;
import com.even.zaro.dto.comment.MentionedUserDto;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.entity.Comment;
import com.even.zaro.entity.Post;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.comment.CommentException;
import com.even.zaro.mapper.CommentMapper;
import com.even.zaro.repository.CommentRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final UserService userService;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponseDto createComment(Long postId, CommentRequestDto requestDto, JwtUserInfoDto userInfoDto, int pageSize) {
        Long currentUserId = userInfoDto.getUserId();

        Post post = postService.findPostOrThrow(postId);
        User user = userService.findUserById(currentUserId);

        User mentionedUser = null;
        String mentionedNickname = requestDto.getMentionedNickname();
        if (mentionedNickname != null && !mentionedNickname.isBlank()) {
            mentionedUser = userRepository.findByNickname(mentionedNickname)
                    .orElseThrow(() -> new CommentException(ErrorCode.MENTIONED_USER_NOT_FOUND));
        }

        validateCommentLength(requestDto.getContent());

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(requestDto.getContent())
                .mentionedUser(mentionedUser)
                .build();

        commentRepository.save(comment);
        post.changeCommentCount(post.getCommentCount() + 1);
        post.updateScore();
        postRepository.save(post);

        int commentLocatedPage = calculateTotalPages(post, pageSize);

        return commentMapper.toCreateDto(comment, currentUserId, commentLocatedPage);
    }

    @Transactional(readOnly = true)
    public CommentPageResponse readAllComments(Long postId, Pageable pageable, JwtUserInfoDto userInfoDto) {
        Long currentUserId = userInfoDto.getUserId();

        Page<CommentResponseDto> page = commentRepository.findByPostIdAndIsDeletedFalse(postId, pageable)
                .map(comment -> commentMapper.toListDto(comment, currentUserId));

        int totalComments = (int) page.getTotalElements();

        return new CommentPageResponse(page, totalComments);
    }

    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto, JwtUserInfoDto userInfoDto) {
        Long currentUserId = userInfoDto.getUserId();

        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.isReported()) {
            throw new CommentException(ErrorCode.COMMENT_REPORTED_CANNOT_EDIT);
        }

        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new CommentException(ErrorCode.NOT_COMMENT_OWNER);
        }

        validateCommentLength(requestDto.getContent());

        comment.updateContent(requestDto.getContent());

        return commentMapper.toUpdateDto(comment, currentUserId);
    }

    @Transactional
    public void softDeleteComment(Long commentId, JwtUserInfoDto userInfoDto) {
        Long currentUserId = userInfoDto.getUserId();
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new CommentException(ErrorCode.NOT_COMMENT_OWNER);
        }

        comment.softDelete();

        Post post = comment.getPost();
        post.changeCommentCount(Math.max(0, post.getCommentCount() - 1));
        post.updateScore();
        postRepository.save(post);
    }

    private void validateCommentLength(String content) {
        if (content.length() > 500) {
            throw new CommentException(ErrorCode.COMMENT_TOO_LONG);
        }
    }

    private int calculateTotalPages(Post post, int pageSize) {
        int totalComments = commentRepository.countByPostAndIsDeletedFalse(post);
        return (int) Math.ceil((double) totalComments / pageSize);
    }
}
