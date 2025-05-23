package com.even.zaro.repository;

import com.even.zaro.dto.post.PostSearchDto;
import com.even.zaro.entity.Post;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.even.zaro.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostSearchRepositoryImpl implements PostSearchRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Page<PostSearchDto> searchPosts(String category, String keyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.isDeleted.isFalse());

        if (category != null && !category.isBlank()) {
            builder.and(post.category.eq(Post.Category.valueOf(category)));
        }

        if (keyword != null && !keyword.isBlank()) {
            builder.and(
                    post.title.containsIgnoreCase(keyword)
                            .or(post.content.containsIgnoreCase(keyword))
            );
        }

        List<PostSearchDto> content = queryFactory
                .select(Projections.constructor(PostSearchDto.class,
                        post.id,
                        post.title,
                        post.content,
                        post.thumbnailImage,
                        post.category.stringValue(),
                        post.tag.stringValue(),
                        post.likeCount,
                        post.commentCount,
                        post.createdAt
                ))
                .from(post)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}