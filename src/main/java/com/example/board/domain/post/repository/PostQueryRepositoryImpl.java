package com.example.board.domain.post.repository;

import com.example.board.domain.post.dto.query.PostListQueryDto;
import com.example.board.domain.post.dto.query.PostSearchCondition;
import com.example.board.domain.post.dto.request.PostSortType;
import com.example.board.domain.post.entity.PostStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.board.domain.category.entity.QCategory.category;
import static com.example.board.domain.member.entity.QMember.member;
import static com.example.board.domain.post.entity.QPost.post;
import static com.example.board.domain.postlike.entity.QPostLike.postLike;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl
        implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostListQueryDto> searchPosts(
            PostSearchCondition condition,
            Pageable pageable
    ) {
        List<PostListQueryDto> content =
                queryFactory
                        .select(
                                Projections.constructor(
                                        PostListQueryDto.class,
                                        post.id,
                                        post.title,
                                        member.id,
                                        member.nickname,
                                        category.id,
                                        category.name,
                                        post.viewCount,
                                        postLike.id.count(),
                                        post.createdAt
                                )
                        )
                        .from(post)
                        .join(post.author, member)
                        .join(post.category, category)
                        .leftJoin(postLike)
                        .on(postLike.post.eq(post))
                        .where(
                                post.status.eq(
                                        PostStatus.PUBLISHED
                                ),
                                keywordContains(
                                        condition.keyword()
                                ),
                                authorContains(
                                        condition.author()
                                ),
                                categoryIdEquals(
                                        condition.categoryId()
                                )
                        )
                        .groupBy(
                                post.id,
                                post.title,
                                member.id,
                                member.nickname,
                                category.id,
                                category.name,
                                post.viewCount,
                                post.createdAt
                        )
                        .orderBy(
                                orderSpecifiers(
                                        condition.sort()
                                )
                        )
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        long total = countPosts(condition);

        return new PageImpl<>(
                content,
                pageable,
                total
        );
    }

    private long countPosts(
            PostSearchCondition condition
    ) {
        Long count = queryFactory
                .select(post.count())
                .from(post)
                .join(post.author, member)
                .join(post.category, category)
                .where(
                        post.status.eq(
                                PostStatus.PUBLISHED
                        ),
                        keywordContains(
                                condition.keyword()
                        ),
                        authorContains(
                                condition.author()
                        ),
                        categoryIdEquals(
                                condition.categoryId()
                        )
                )
                .fetchOne();

        return count == null ? 0L : count;
    }

    private BooleanExpression keywordContains(
            String keyword
    ) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        return post.title
                .containsIgnoreCase(keyword)
                .or(
                        post.content
                                .containsIgnoreCase(keyword)
                );
    }

    private BooleanExpression authorContains(
            String author
    ) {
        if (!StringUtils.hasText(author)) {
            return null;
        }

        return member.nickname
                .containsIgnoreCase(author);
    }

    private BooleanExpression categoryIdEquals(
            Long categoryId
    ) {
        if (categoryId == null) {
            return null;
        }

        return category.id.eq(categoryId);
    }

    private OrderSpecifier<?>[] orderSpecifiers(
            PostSortType sortType
    ) {
        return switch (sortType) {
            case LIKES -> new OrderSpecifier<?>[]{
                    postLike.id.count().desc(),
                    post.id.desc()
            };

            case VIEWS -> new OrderSpecifier<?>[]{
                    post.viewCount.desc(),
                    post.id.desc()
            };

            case LATEST -> new OrderSpecifier<?>[]{
                    post.createdAt.desc(),
                    post.id.desc()
            };
        };
    }
}