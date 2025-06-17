package com.even.zaro.mapper;

import com.even.zaro.dto.comment.CommentResponseDto;
import com.even.zaro.dto.comment.MentionedUserDto;
import com.even.zaro.entity.Comment;
import com.even.zaro.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    default OffsetDateTime map(LocalDateTime time) {
        return time != null ? time.atOffset(ZoneOffset.UTC) : null;
    }

    @Named("isEdited")
    static boolean isEdited(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return !createdAt.truncatedTo(ChronoUnit.SECONDS)
                .isEqual(updatedAt.truncatedTo(ChronoUnit.SECONDS));
    }

    @Named("isMine")
    static boolean isMine(User writer, Long currentUserId) {
        return writer != null && writer.getId().equals(currentUserId);
    }

    @Named("toMentionedUserDto")
    static MentionedUserDto toMentionedUserDto(User mentionedUser) {
        return mentionedUser != null
                ? new MentionedUserDto(mentionedUser.getId(), mentionedUser.getNickname())
                : null;
    }

    @Mapping(source = "comment.user.id", target = "userId")
    @Mapping(source = "comment.user.nickname", target = "nickname")
    @Mapping(source = "comment.user.profileImage", target = "profileImage")
    @Mapping(source = "comment.user.liveAloneDate", target = "liveAloneDate")
    @Mapping(target = "createdAt", source = "comment.createdAt")
    @Mapping(target = "updatedAt", source = "comment.updatedAt")
    @Mapping(target = "isEdited", expression = "java(com.even.zaro.mapper.CommentMapper.isEdited(comment.getCreatedAt(), comment.getUpdatedAt()))")
    @Mapping(target = "isMine", expression = "java(com.even.zaro.mapper.CommentMapper.isMine(comment.getUser(), currentUserId))")
    @Mapping(target = "mentionedUser", expression = "java(com.even.zaro.mapper.CommentMapper.toMentionedUserDto(comment.getMentionedUser()))")
    @Mapping(target = "commentLocatedPage", source = "commentLocatedPage")
    CommentResponseDto toCreateDto(Comment comment, Long currentUserId, Integer commentLocatedPage);

    @Mapping(target = "content", expression = "java(comment.isReported() ? \"신고로 삭제된 댓글입니다.\" : comment.getContent())")
    @Mapping(source = "comment.user.id", target = "userId")
    @Mapping(target = "nickname", expression = "java(comment.getUser().getStatus() == com.even.zaro.entity.Status.DELETED ? \"알 수 없는 사용자\" : comment.getUser().getNickname())")
    @Mapping(target = "profileImage", expression = "java(comment.getUser().getStatus() == com.even.zaro.entity.Status.DELETED ? null : comment.getUser().getProfileImage())")
    @Mapping(source = "comment.user.liveAloneDate", target = "liveAloneDate")
    @Mapping(target = "createdAt", source = "comment.createdAt")
    @Mapping(target = "updatedAt", source = "comment.updatedAt")
    @Mapping(target = "isEdited", expression = "java(com.even.zaro.mapper.CommentMapper.isEdited(comment.getCreatedAt(), comment.getUpdatedAt()))")
    @Mapping(target = "isMine", expression = "java(com.even.zaro.mapper.CommentMapper.isMine(comment.getUser(), currentUserId))")
    @Mapping(target = "mentionedUser", expression = "java(com.even.zaro.mapper.CommentMapper.toMentionedUserDto(comment.getMentionedUser()))")
    @Mapping(target = "commentLocatedPage", ignore = true)
    CommentResponseDto toListDto(Comment comment, Long currentUserId);

    @Mapping(source = "comment.user.id", target = "userId")
    @Mapping(source = "comment.user.nickname", target = "nickname")
    @Mapping(source = "comment.user.profileImage", target = "profileImage")
    @Mapping(source = "comment.user.liveAloneDate", target = "liveAloneDate")
    @Mapping(target = "createdAt", source = "comment.createdAt")
    @Mapping(target = "updatedAt", source = "comment.updatedAt")
    @Mapping(target = "isEdited", expression = "java(com.even.zaro.mapper.CommentMapper.isEdited(comment.getCreatedAt(), comment.getUpdatedAt()))")
    @Mapping(target = "isMine", expression = "java(com.even.zaro.mapper.CommentMapper.isMine(comment.getUser(), currentUserId))")
    @Mapping(target = "mentionedUser", expression = "java(com.even.zaro.mapper.CommentMapper.toMentionedUserDto(comment.getMentionedUser()))")
    @Mapping(target = "commentLocatedPage", ignore = true)
    CommentResponseDto toUpdateDto(Comment comment, Long currentUserId);
}
