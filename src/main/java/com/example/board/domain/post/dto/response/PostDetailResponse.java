package com.example.board.domain.post.dto.response;

import com.example.board.domain.post.dto.query.PostDetailQueryDto;

import java.time.LocalDateTime;

public record PostDetailResponse(
        Long postId,
        String title,
        String content,
        long viewCount,
        long commentCount,
        AuthorResponse author,
        CategoryResponse category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PostDetailResponse from(
            PostDetailQueryDto dto,
            long commentCount
    ) {
        return new PostDetailResponse(
                dto.postId(),
                dto.title(),
                dto.content(),
                dto.viewCount(),
                commentCount,
                new AuthorResponse(
                        dto.authorId(),
                        dto.authorNickname()
                ),
                new CategoryResponse(
                        dto.categoryId(),
                        dto.categoryName()
                ),
                dto.createdAt(),
                dto.updatedAt()
        );
    }

    public record AuthorResponse(
            Long memberId,
            String nickname
    ) {
    }

    public record CategoryResponse(
            Long categoryId,
            String name
    ) {
    }
}