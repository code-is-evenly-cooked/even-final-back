package com.even.zaro.service;

import com.even.zaro.dto.PageResponse;
import com.even.zaro.dto.comment.CommentResponseDto;
import com.even.zaro.dto.comment.CommentRequestDto;
import com.even.zaro.dto.comment.MentionedUserDto;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.entity.Comment;
import com.even.zaro.entity.Post;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.comment.CommentException;
import com.even.zaro.global.exception.post.PostException;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.CommentRepository;
import com.even.zaro.repository.PostRepository;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDto createComment(Long postId, CommentRequestDto requestDto, JwtUserInfoDto userInfoDto) {
        Long currentUserId = userInfoDto.getUserId();
        if (requestDto.getContent() == null || requestDto.getContent().isBlank()) {
            throw new CommentException(ErrorCode.COMMENT_CONTENT_BLANK);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        User mentionedUser = null;
        String mentionedNickname = requestDto.getMentionedNickname();
        if (mentionedNickname != null && !mentionedNickname.isBlank()) {
            mentionedUser = userRepository.findByNickname(mentionedNickname)
                    .orElseThrow(() -> new CommentException(ErrorCode.MENTIONED_USER_NOT_FOUND));
        }

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

        return toDto(comment, currentUserId);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponseDto> readAllComments(Long postId, Pageable pageable, JwtUserInfoDto userInfoDto) {
        Long currentUserId = userInfoDto.getUserId();

        // 게시글 존재 여부 확인
        if (!postRepository.existsByIdAndIsDeletedFalse(postId)) {
            throw new PostException(ErrorCode.POST_NOT_FOUND);
        }

        Page<CommentResponseDto> page = commentRepository.findByPostIdAndIsDeletedFalse(postId, pageable)
                .map(comment -> toDto(comment, currentUserId));
        return new PageResponse<>(page);
    }

    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto, JwtUserInfoDto userInfoDto) {
        Long currentUserId = userInfoDto.getUserId();
        if (requestDto.getContent() == null || requestDto.getContent().isBlank()) {
            throw new CommentException(ErrorCode.COMMENT_CONTENT_BLANK);
        }

        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new CommentException(ErrorCode.NOT_COMMENT_OWNER);
        }

        comment.updateContent(requestDto.getContent());
        return toDto(comment, currentUserId);
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

    // 공통 응답
    private CommentResponseDto toDto(Comment comment, Long currentUserId) {
        User writer = comment.getUser();

        boolean isMine = writer.getId().equals(currentUserId);
        LocalDateTime createdAt = comment.getCreatedAt();
        LocalDateTime updatedAt = comment.getUpdatedAt();

        boolean isEdited = !createdAt.truncatedTo(ChronoUnit.SECONDS)
                .isEqual(updatedAt.truncatedTo(ChronoUnit.SECONDS));


        User mentioned = comment.getMentionedUser();
        MentionedUserDto mentionedUser = null;
        if (mentioned != null) {
            mentionedUser = new MentionedUserDto(mentioned.getId(), mentioned.getNickname());
        }

        String content = comment.isReported()
                ? "신고로 삭제된 댓글입니다."
                : comment.getContent();

        return new CommentResponseDto(
                comment.getId(),
                content,
                writer.getNickname(),
                writer.getProfileImage(),
                writer.getLiveAloneDate(),
                createdAt,
                updatedAt,
                isEdited,
                isMine,
                mentionedUser
        );
    }
}
