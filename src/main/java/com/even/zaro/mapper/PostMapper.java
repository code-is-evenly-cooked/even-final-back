package com.even.zaro.mapper;

import com.even.zaro.dto.post.HomePostPreviewResponse;
import com.even.zaro.dto.post.PostDetailResponse;
import com.even.zaro.dto.post.PostPreviewDto;
import com.even.zaro.dto.post.PostRankResponseDto;
import com.even.zaro.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface PostMapper {

    default OffsetDateTime map(LocalDateTime time) {
        return time != null ? time.atOffset(ZoneOffset.UTC) : null;
    }

    @Mapping(source = "id", target = "postId")
    @Mapping(source = "user.id", target = "user.userId")
    @Mapping(source = "user.nickname", target = "user.nickname")
    @Mapping(source = "user.profileImage", target = "user.profileImage")
    @Mapping(source = "user.liveAloneDate", target = "user.liveAloneDate")
    PostDetailResponse toPostDetailDto(Post post);

    @Mapping(source = "id", target = "postId")
    @Mapping(target = "writerNickname", expression = "java(post.getUser().getStatus() == com.even.zaro.entity.Status.DELETED ? \"알 수 없는 사용자\" : post.getUser().getNickname())")
    @Mapping(target = "writerProfileImage", expression = "java(post.getUser().getStatus() == com.even.zaro.entity.Status.DELETED ? null : post.getUser().getProfileImage())")
    @Mapping(target = "content", expression = "java(stripHtmlTags(post.getContent()))")
    PostPreviewDto toPostPreviewDto(Post post);

    @Mapping(source = "id", target = "postId")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd")
    HomePostPreviewResponse.SimplePostDto toSimplePostDto(Post post);

    @Mappings({
            @Mapping(source = "id", target = "postId"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "content", target = "content"),
            @Mapping(source = "thumbnailImage", target = "thumbnailImage"),
            @Mapping(source = "likeCount", target = "likeCount"),
            @Mapping(source = "commentCount", target = "commentCount"),
            @Mapping(source = "user.profileImage", target = "writerProfileImage"),
            @Mapping(source = "user.nickname", target = "writerNickname"),
            @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd")
    })
    HomePostPreviewResponse.RandomBuyPostDto toRandomBuyDto(Post post);


    default String stripHtmlTags(String html) {
        if (html == null) return "";
        html = html.replace("&lt;", "<").replace("&gt;", ">");
        return html
                .replaceAll("(?i)<br\\s*/?>", "")
                .replaceAll("<[^>]*>", "")
                .replaceAll("\\\\","")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
