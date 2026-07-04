package com.example.board.domain.post.repository;

import com.example.board.domain.category.entity.Category;
import com.example.board.domain.member.entity.Member;
import com.example.board.domain.post.dto.query.PostListQueryDto;
import com.example.board.domain.post.dto.query.PostSearchCondition;
import com.example.board.domain.post.dto.request.PostSortType;
import com.example.board.domain.post.entity.Post;
import com.example.board.global.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
class PostQueryRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("키워드와 카테고리 조건으로 게시글을 조회한다")
    void searchPostsByKeywordAndCategory() {
        // given
        Member member = Member.create(
                "user@example.com",
                "encoded-password",
                "backend"
        );

        Category study =
                Category.create("공부 기록");

        Category free =
                Category.create("자유");

        entityManager.persist(member);
        entityManager.persist(study);
        entityManager.persist(free);

        Post matchingPost = Post.create(
                member,
                study,
                "QueryDSL 동적 쿼리",
                "Spring에서 QueryDSL을 사용합니다."
        );

        Post differentCategoryPost = Post.create(
                member,
                free,
                "QueryDSL 기초",
                "다른 카테고리입니다."
        );

        Post differentKeywordPost = Post.create(
                member,
                study,
                "JPA 영속성 컨텍스트",
                "변경 감지를 공부합니다."
        );

        entityManager.persist(matchingPost);
        entityManager.persist(differentCategoryPost);
        entityManager.persist(differentKeywordPost);

        entityManager.flush();
        entityManager.clear();

        PostSearchCondition condition =
                new PostSearchCondition(
                        "querydsl",
                        null,
                        study.getId(),
                        PostSortType.LATEST
                );

        // when
        Page<PostListQueryDto> result =
                postRepository.searchPosts(
                        condition,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(result.getTotalElements())
                .isEqualTo(1);

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(
                result.getContent()
                        .getFirst()
                        .title()
        ).isEqualTo("QueryDSL 동적 쿼리");
    }

    @Test
    @DisplayName("조회 수가 높은 게시글부터 조회한다")
    void searchPostsOrderByViews() {
        // given
        Member member = Member.create(
                "user@example.com",
                "encoded-password",
                "backend"
        );

        Category category =
                Category.create("자유");

        entityManager.persist(member);
        entityManager.persist(category);

        Post lowViewPost = Post.create(
                member,
                category,
                "조회 수 낮은 게시글",
                "내용"
        );

        Post highViewPost = Post.create(
                member,
                category,
                "조회 수 높은 게시글",
                "내용"
        );

        entityManager.persist(lowViewPost);
        entityManager.persist(highViewPost);

        entityManager.flush();

        postRepository.increaseViewCount(
                highViewPost.getId(),
                com.example.board.domain.post.entity
                        .PostStatus.PUBLISHED
        );

        postRepository.increaseViewCount(
                highViewPost.getId(),
                com.example.board.domain.post.entity
                        .PostStatus.PUBLISHED
        );

        entityManager.clear();

        PostSearchCondition condition =
                new PostSearchCondition(
                        null,
                        null,
                        null,
                        PostSortType.VIEWS
                );

        // when
        Page<PostListQueryDto> result =
                postRepository.searchPosts(
                        condition,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(
                result.getContent()
                        .getFirst()
                        .title()
        ).isEqualTo("조회 수 높은 게시글");
    }

    @Test
    @DisplayName("삭제된 게시글은 목록에 나타나지 않는다")
    void deletedPostIsExcluded() {
        // given
        Member member = Member.create(
                "user@example.com",
                "encoded-password",
                "backend"
        );

        Category category =
                Category.create("자유");

        entityManager.persist(member);
        entityManager.persist(category);

        Post publishedPost = Post.create(
                member,
                category,
                "공개 게시글",
                "내용"
        );

        Post deletedPost = Post.create(
                member,
                category,
                "삭제 게시글",
                "내용"
        );

        deletedPost.delete();

        entityManager.persist(publishedPost);
        entityManager.persist(deletedPost);

        entityManager.flush();
        entityManager.clear();

        PostSearchCondition condition =
                new PostSearchCondition(
                        null,
                        null,
                        null,
                        PostSortType.LATEST
                );

        // when
        Page<PostListQueryDto> result =
                postRepository.searchPosts(
                        condition,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(result.getContent())
                .extracting(PostListQueryDto::title)
                .containsExactly("공개 게시글");
    }
}