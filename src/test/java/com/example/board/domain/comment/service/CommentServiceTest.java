package com.example.board.domain.comment.service;

import com.example.board.domain.category.entity.Category;
import com.example.board.domain.comment.dto.request.CommentCreateRequest;
import com.example.board.domain.comment.dto.response.CommentCreateResponse;
import com.example.board.domain.comment.entity.Comment;
import com.example.board.domain.comment.repository.CommentRepository;
import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.entity.MemberStatus;
import com.example.board.domain.member.repository.MemberRepository;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.entity.PostStatus;
import com.example.board.domain.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(
                commentRepository,
                postRepository,
                memberRepository
        );
    }

    @Test
    @DisplayName("로그인한 회원이 댓글을 작성한다")
    void createCommentSuccess() {
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

        Member postAuthor = Member.create(
                "author@example.com",
                "encoded-password",
                "author"
        );

        Category category =
                Category.create("자유");

        Post post = Post.create(
                postAuthor,
                category,
                "게시글 제목",
                "게시글 내용"
        );

        ReflectionTestUtils.setField(
                post,
                "id",
                postId
        );

        given(
                memberRepository.findByIdAndStatus(
                        memberId,
                        MemberStatus.ACTIVE
                )
        ).willReturn(Optional.of(member));

        given(
                postRepository.findByIdAndStatus(
                        postId,
                        PostStatus.PUBLISHED
                )
        ).willReturn(Optional.of(post));

        given(
                commentRepository.save(
                        any(Comment.class)
                )
        ).willAnswer(invocation -> {
            Comment comment =
                    invocation.getArgument(0);

            ReflectionTestUtils.setField(
                    comment,
                    "id",
                    100L
            );

            return comment;
        });

        CommentCreateRequest request =
                new CommentCreateRequest(
                        "  좋은 글 감사합니다.  "
                );

        // when
        CommentCreateResponse response =
                commentService.createComment(
                        memberId,
                        postId,
                        request
                );

        // then
        assertThat(response.commentId())
                .isEqualTo(100L);

        ArgumentCaptor<Comment> captor =
                ArgumentCaptor.forClass(
                        Comment.class
                );

        then(commentRepository)
                .should()
                .save(captor.capture());

        Comment savedComment =
                captor.getValue();

        assertThat(savedComment.getPost())
                .isSameAs(post);

        assertThat(savedComment.getAuthor())
                .isSameAs(member);

        assertThat(savedComment.getContent())
                .isEqualTo("좋은 글 감사합니다.");
    }
}