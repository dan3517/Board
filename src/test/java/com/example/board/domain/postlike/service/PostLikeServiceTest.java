package com.example.board.domain.postlike.service;

import com.example.board.domain.category.entity.Category;
import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.entity.MemberStatus;
import com.example.board.domain.member.repository.MemberRepository;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.entity.PostStatus;
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.domain.postlike.dto.response.PostLikeResponse;
import com.example.board.domain.postlike.entity.PostLike;
import com.example.board.domain.postlike.repository.PostLikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    private PostLikeService postLikeService;

    @BeforeEach
    void setUp() {
        postLikeService = new PostLikeService(
                postLikeRepository,
                postRepository,
                memberRepository
        );
    }

    @Test
    @DisplayName("좋아요가 없으면 새 좋아요를 등록한다")
    void likePostSuccess() {
        // given
        Long memberId = 1L;
        Long postId = 10L;

        Member member = Member.create(
                "user@example.com",
                "encoded-password",
                "reader"
        );

        ReflectionTestUtils.setField(
                member,
                "id",
                memberId
        );

        Member author = Member.create(
                "author@example.com",
                "encoded-password",
                "author"
        );

        Category category =
                Category.create("자유");

        Post post = Post.create(
                author,
                category,
                "게시글",
                "내용"
        );

        ReflectionTestUtils.setField(
                post,
                "id",
                postId
        );

        given(
                postRepository
                        .findByIdAndStatusForUpdate(
                                postId,
                                PostStatus.PUBLISHED
                        )
        ).willReturn(Optional.of(post));

        given(
                memberRepository.findByIdAndStatus(
                        memberId,
                        MemberStatus.ACTIVE
                )
        ).willReturn(Optional.of(member));

        given(
                postLikeRepository
                        .existsByPostIdAndMemberId(
                                postId,
                                memberId
                        )
        ).willReturn(false);

        given(
                postLikeRepository.countByPostId(
                        postId
                )
        ).willReturn(1L);

        // when
        PostLikeResponse response =
                postLikeService.likePost(
                        memberId,
                        postId
                );

        // then
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1L);

        then(postLikeRepository)
                .should()
                .save(any(PostLike.class));
    }

    @Test
    @DisplayName("이미 좋아요한 게시글에 다시 요청해도 중복 저장하지 않는다")
    void likePostIsIdempotent() {
        // given
        Long memberId = 1L;
        Long postId = 10L;

        Member member = Member.create(
                "user@example.com",
                "encoded-password",
                "reader"
        );

        ReflectionTestUtils.setField(
                member,
                "id",
                memberId
        );

        Member author = Member.create(
                "author@example.com",
                "encoded-password",
                "author"
        );

        Category category =
                Category.create("자유");

        Post post = Post.create(
                author,
                category,
                "게시글",
                "내용"
        );

        ReflectionTestUtils.setField(
                post,
                "id",
                postId
        );

        given(
                postRepository.findByIdAndStatusForUpdate(
                        postId,
                        PostStatus.PUBLISHED
                )
        ).willReturn(Optional.of(post));

        given(
                memberRepository.findByIdAndStatus(
                        memberId,
                        MemberStatus.ACTIVE
                )
        ).willReturn(Optional.of(member));

        given(
                postLikeRepository
                        .existsByPostIdAndMemberId(
                                postId,
                                memberId
                        )
        ).willReturn(true);

        given(
                postLikeRepository.countByPostId(
                        postId
                )
        ).willReturn(1L);

        // when
        PostLikeResponse response =
                postLikeService.likePost(
                        memberId,
                        postId
                );

        // then
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1L);

        then(postLikeRepository)
                .should(never())
                .save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요를 취소한다")
    void unlikePostSuccess() {
        // given
        Long memberId = 1L;
        Long postId = 10L;

        Member author = Member.create(
                "author@example.com",
                "encoded-password",
                "author"
        );

        Category category =
                Category.create("자유");

        Post post = Post.create(
                author,
                category,
                "게시글",
                "내용"
        );

        ReflectionTestUtils.setField(
                post,
                "id",
                postId
        );

        given(
                postRepository.findByIdAndStatusForUpdate(
                        postId,
                        PostStatus.PUBLISHED
                )
        ).willReturn(Optional.of(post));

        given(
                postLikeRepository
                        .deleteByPostIdAndMemberId(
                                postId,
                                memberId
                        )
        ).willReturn(1);

        given(
                postLikeRepository.countByPostId(
                        postId
                )
        ).willReturn(0L);

        // when
        PostLikeResponse response =
                postLikeService.unlikePost(
                        memberId,
                        postId
                );

        // then
        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isZero();
    }
}