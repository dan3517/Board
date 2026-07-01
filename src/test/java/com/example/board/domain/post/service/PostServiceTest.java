package com.example.board.domain.post.service;

import com.example.board.domain.category.entity.Category;
import com.example.board.domain.category.entity.CategoryStatus;
import com.example.board.domain.category.repository.CategoryRepository;
import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.entity.MemberStatus;
import com.example.board.domain.member.repository.MemberRepository;
import com.example.board.domain.post.dto.request.PostCreateRequest;
import com.example.board.domain.post.dto.response.PostCreateResponse;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.entity.PostStatus;
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostRepository postRepository;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(
                memberRepository,
                categoryRepository,
                postRepository
        );
    }

    @Test
    @DisplayName("로그인한 회원이 게시글을 작성한다")
    void createPostSuccess() {
        // given
        Long memberId = 1L;
        Long categoryId = 10L;

        PostCreateRequest request =
                new PostCreateRequest(
                        categoryId,
                        "  JPA 공부 기록  ",
                        "영속성 컨텍스트를 공부했습니다."
                );

        Member member = Member.create(
                "user@example.com",
                "encoded-password",
                "backend"
        );

        ReflectionTestUtils.setField(
                member,
                "id",
                memberId
        );

        Category category =
                Category.create("공부 기록");

        ReflectionTestUtils.setField(
                category,
                "id",
                categoryId
        );

        given(
                memberRepository.findByIdAndStatus(
                        memberId,
                        MemberStatus.ACTIVE
                )
        ).willReturn(Optional.of(member));

        given(
                categoryRepository.findByIdAndStatus(
                        categoryId,
                        CategoryStatus.ACTIVE
                )
        ).willReturn(Optional.of(category));

        given(
                postRepository.save(any(Post.class))
        ).willAnswer(invocation -> {
            Post post = invocation.getArgument(0);

            ReflectionTestUtils.setField(
                    post,
                    "id",
                    100L
            );

            return post;
        });

        // when
        PostCreateResponse response =
                postService.createPost(
                        memberId,
                        request
                );

        // then
        assertThat(response.postId())
                .isEqualTo(100L);

        ArgumentCaptor<Post> postCaptor =
                ArgumentCaptor.forClass(Post.class);

        then(postRepository)
                .should()
                .save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();

        assertThat(savedPost.getAuthor())
                .isSameAs(member);

        assertThat(savedPost.getCategory())
                .isSameAs(category);

        assertThat(savedPost.getTitle())
                .isEqualTo("JPA 공부 기록");

        assertThat(savedPost.getContent())
                .isEqualTo(
                        "영속성 컨텍스트를 공부했습니다."
                );

        assertThat(savedPost.getViewCount())
                .isZero();

        assertThat(savedPost.getStatus())
                .isEqualTo(PostStatus.PUBLISHED);
    }

    @Test
    @DisplayName("활성 회원을 찾을 수 없으면 게시글 작성에 실패한다")
    void createPostFailsWhenMemberNotFound() {
        // given
        Long memberId = 1L;

        PostCreateRequest request =
                new PostCreateRequest(
                        10L,
                        "제목",
                        "내용"
                );

        given(
                memberRepository.findByIdAndStatus(
                        memberId,
                        MemberStatus.ACTIVE
                )
        ).willReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(
                () -> postService.createPost(
                        memberId,
                        request
                )
        );

        // then
        assertThat(throwable)
                .isInstanceOf(BusinessException.class);

        BusinessException exception =
                (BusinessException) throwable;

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.MEMBER_NOT_FOUND
                );

        then(categoryRepository)
                .shouldHaveNoInteractions();

        then(postRepository)
                .shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("활성 카테고리를 찾을 수 없으면 게시글 작성에 실패한다")
    void createPostFailsWhenCategoryNotFound() {
        // given
        Long memberId = 1L;
        Long categoryId = 10L;

        PostCreateRequest request =
                new PostCreateRequest(
                        categoryId,
                        "제목",
                        "내용"
                );

        Member member = Member.create(
                "user@example.com",
                "encoded-password",
                "backend"
        );

        given(
                memberRepository.findByIdAndStatus(
                        memberId,
                        MemberStatus.ACTIVE
                )
        ).willReturn(Optional.of(member));

        given(
                categoryRepository.findByIdAndStatus(
                        categoryId,
                        CategoryStatus.ACTIVE
                )
        ).willReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(
                () -> postService.createPost(
                        memberId,
                        request
                )
        );

        // then
        assertThat(throwable)
                .isInstanceOf(BusinessException.class);

        BusinessException exception =
                (BusinessException) throwable;

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.CATEGORY_NOT_FOUND
                );

        then(postRepository)
                .shouldHaveNoInteractions();
    }
}