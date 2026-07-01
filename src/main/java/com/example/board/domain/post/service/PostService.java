package com.example.board.domain.post.service;

import com.example.board.domain.category.entity.Category;
import com.example.board.domain.category.entity.CategoryStatus;
import com.example.board.domain.category.repository.CategoryRepository;
import com.example.board.domain.comment.entity.CommentStatus;
import com.example.board.domain.comment.repository.CommentRepository;
import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.entity.MemberRole;
import com.example.board.domain.member.entity.MemberStatus;
import com.example.board.domain.member.repository.MemberRepository;
import com.example.board.domain.post.dto.query.PostDetailQueryDto;
import com.example.board.domain.post.dto.query.PostListQueryDto;
import com.example.board.domain.post.dto.request.PostCreateRequest;
import com.example.board.domain.post.dto.request.PostSearchRequest;
import com.example.board.domain.post.dto.request.PostUpdateRequest;
import com.example.board.domain.post.dto.response.PostCreateResponse;
import com.example.board.domain.post.dto.response.PostDetailResponse;
import com.example.board.domain.post.dto.response.PostListResponse;
import com.example.board.domain.post.dto.response.PostUpdateResponse;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.entity.PostStatus;
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.domain.postlike.repository.PostLikeRepository;
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
public class PostService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    public PostCreateResponse createPost(
            Long memberId,
            PostCreateRequest request
    ) {
        Member author = findActiveMember(memberId);

        Category category = findActiveCategory(
                request.categoryId()
        );

        Post post = Post.create(
                author,
                category,
                normalizeTitle(request.title()),
                request.content()
        );

        Post savedPost =
                postRepository.save(post);

        return PostCreateResponse.from(savedPost);
    }

    public PostDetailResponse getPost(
            Long postId,
            Long currentMemberId
    ) {
        int updatedRowCount =
                postRepository.increaseViewCount(
                        postId,
                        PostStatus.PUBLISHED
                );

        if (updatedRowCount == 0) {
            throw new BusinessException(
                    ErrorCode.POST_NOT_FOUND
            );
        }

        PostDetailQueryDto post =
                postRepository
                        .findDetailByIdAndStatus(
                                postId,
                                PostStatus.PUBLISHED
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.POST_NOT_FOUND
                                )
                        );

        long commentCount =
                commentRepository
                        .countByPostIdAndStatus(
                                postId,
                                CommentStatus.PUBLISHED
                        );

        long likeCount =
                postLikeRepository.countByPostId(
                        postId
                );

        boolean likedByMe =
                currentMemberId != null
                        && postLikeRepository
                        .existsByPostIdAndMemberId(
                                postId,
                                currentMemberId
                        );

        return PostDetailResponse.from(
                post,
                commentCount,
                likeCount,
                likedByMe
        );
    }

    public PostUpdateResponse updatePost(
            Long memberId,
            MemberRole memberRole,
            Long postId,
            PostUpdateRequest request
    ) {
        Post post = findPublishedPost(postId);

        validateModificationAuthority(
                post,
                memberId,
                memberRole
        );

        Category category = findActiveCategory(
                request.categoryId()
        );

        post.update(
                category,
                normalizeTitle(request.title()),
                request.content()
        );

        return PostUpdateResponse.from(post);
    }

    public void deletePost(
            Long memberId,
            MemberRole memberRole,
            Long postId
    ) {
        Post post = findPublishedPost(postId);

        validateModificationAuthority(
                post,
                memberId,
                memberRole
        );

        post.delete();
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

    private void validateModificationAuthority(
            Post post,
            Long memberId,
            MemberRole memberRole
    ) {
        boolean isAdmin =
                memberRole == MemberRole.ADMIN;

        boolean isAuthor =
                post.isAuthor(memberId);

        if (!isAdmin && !isAuthor) {
            throw new BusinessException(
                    ErrorCode.POST_ACCESS_DENIED
            );
        }
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

    private Category findActiveCategory(
            Long categoryId
    ) {
        return categoryRepository
                .findByIdAndStatus(
                        categoryId,
                        CategoryStatus.ACTIVE
                )
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.CATEGORY_NOT_AVAILABLE
                        )
                );
    }

    private String normalizeTitle(String title) {
        return title.strip();
    }

    @Transactional(readOnly = true)
    public PostListResponse getPosts(
            PostSearchRequest request
    ) {
        Pageable pageable = PageRequest.of(
                request.resolvedPage(),
                request.resolvedSize()
        );

        Page<PostListQueryDto> result =
                postRepository.searchPosts(
                        request.toCondition(),
                        pageable
                );

        return PostListResponse.from(result);
    }
}