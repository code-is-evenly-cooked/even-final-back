package com.even.zaro.service;

import com.even.zaro.dto.comment.CommentResponseDto;
import com.even.zaro.dto.comment.CommentRequestDto;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.entity.Comment;
import com.even.zaro.entity.Post;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
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

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDto createComment(Long postId, CommentRequestDto requestDto, JwtUserInfoDto userInfoDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
        User user = userRepository.findById(userInfoDto.getUserId())
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(requestDto.getContent())
                .build();

        commentRepository.save(comment);

        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                user.getNickname(),
                user.getProfileImage(),
                user.getLiveAloneDate(),
                comment.getCreatedAt(),
                true
        );
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> readAllComments(Long postId, Pageable pageable, JwtUserInfoDto userInfoDto) {
        Long currentUserId = userInfoDto.getUserId();

        return commentRepository.findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(postId, pageable)
                .map(comment -> {
                    User writer = comment.getUser();

                    boolean isMine = writer.getId().equals(currentUserId);

                    return new CommentResponseDto(
                            comment.getId(),
                            comment.getContent(),
                            comment.getUser().getNickname(),
                            comment.getUser().getProfileImage(),
                            comment.getUser().getLiveAloneDate(),
                            comment.getCreatedAt(),
                            isMine
                    );
                });
    }
}
