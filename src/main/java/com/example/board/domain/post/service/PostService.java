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
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

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

        Post savedPost = postRepository.save(post);

        return PostCreateResponse.from(savedPost);
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
                                ErrorCode.CATEGORY_NOT_FOUND
                        )
                );
    }

    private String normalizeTitle(String title) {
        return title.strip();
    }
}