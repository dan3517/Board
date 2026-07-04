package com.example.board.domain.comment.service;

import com.example.board.domain.comment.dto.query.CommentListQueryDto;
import com.example.board.domain.comment.dto.request.CommentCreateRequest;
import com.example.board.domain.comment.dto.request.CommentUpdateRequest;
import com.example.board.domain.comment.dto.response.CommentCreateResponse;
import com.example.board.domain.comment.dto.response.CommentListResponse;
import com.example.board.domain.comment.dto.response.CommentUpdateResponse;
import com.example.board.domain.comment.entity.Comment;
import com.example.board.domain.comment.entity.CommentStatus;
import com.example.board.domain.comment.repository.CommentRepository;
import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.entity.MemberRole;
import com.example.board.domain.member.entity.MemberStatus;
import com.example.board.domain.member.repository.MemberRepository;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.entity.PostStatus;
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private static final int MAX_PAGE_SIZE = 100;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public CommentCreateResponse createComment(
            Long memberId,
            Long postId,
            CommentCreateRequest request
    ) {
        Member author = findActiveMember(memberId);
        Post post = findPublishedPost(postId);

        Comment comment = Comment.create(
                post,
                author,
                normalizeContent(request.content())
        );

        Comment savedComment =
                commentRepository.save(comment);

        return CommentCreateResponse.from(
                savedComment
        );
    }

    @Transactional(readOnly = true)
    public CommentListResponse getComments(
            Long postId,
            int page,
            int size
    ) {
        validatePage(page, size);

        if (!postRepository.existsByIdAndStatus(
                postId,
                PostStatus.PUBLISHED
        )) {
            throw new BusinessException(
                    ErrorCode.POST_NOT_FOUND
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size
        );

        Page<CommentListQueryDto> result =
                commentRepository.findCommentsByPostId(
                        postId,
                        CommentStatus.PUBLISHED,
                        pageable
                );

        return CommentListResponse.from(result);
    }

    public CommentUpdateResponse updateComment(
            Long memberId,
            MemberRole memberRole,
            Long commentId,
            CommentUpdateRequest request
    ) {
        Comment comment =
                findPublishedComment(commentId);

        validateModificationAuthority(
                comment,
                memberId,
                memberRole
        );

        comment.update(
                normalizeContent(request.content())
        );

        return CommentUpdateResponse.from(comment);
    }

    public void deleteComment(
            Long memberId,
            MemberRole memberRole,
            Long commentId
    ) {
        Comment comment =
                findPublishedComment(commentId);

        validateModificationAuthority(
                comment,
                memberId,
                memberRole
        );

        comment.delete();
    }

    private Member findActiveMember(Long memberId) {
        return memberRepository
                .findByIdAndStatus(
                        memberId,
                        MemberStatus.ACTIVE
                )
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.MEMBER_NOT_FOUND
                        )
                );
    }

    private Post findPublishedPost(Long postId) {
        return postRepository
                .findByIdAndStatus(
                        postId,
                        PostStatus.PUBLISHED
                )
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.POST_NOT_FOUND
                        )
                );
    }

    private Comment findPublishedComment(
            Long commentId
    ) {
        return commentRepository
                .findByIdAndStatus(
                        commentId,
                        CommentStatus.PUBLISHED
                )
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.COMMENT_NOT_FOUND
                        )
                );
    }

    private void validateModificationAuthority(
            Comment comment,
            Long memberId,
            MemberRole memberRole
    ) {
        boolean isAdmin =
                memberRole == MemberRole.ADMIN;

        boolean isAuthor =
                comment.isAuthor(memberId);

        if (!isAdmin && !isAuthor) {
            throw new BusinessException(
                    ErrorCode.COMMENT_ACCESS_DENIED
            );
        }
    }

    private void validatePage(
            int page,
            int size
    ) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT
            );
        }
    }

    private String normalizeContent(String content) {
        return content.strip();
    }
}