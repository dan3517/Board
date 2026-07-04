package com.example.board.domain.post.dto.response;

import com.example.board.domain.post.dto.query.PostListQueryDto;

import java.time.LocalDateTime;

public record PostListItemResponse(
        Long postId,
        String title,
        AuthorResponse author,
        CategoryResponse category,
        long viewCount,
        long likeCount,
        LocalDateTime createdAt
) {

    public static PostListItemResponse from(
            PostListQueryDto dto
    ) {
        return new PostListItemResponse(
                dto.postId(),
                dto.title(),
                new AuthorResponse(
                        dto.authorId(),
                        dto.authorNickname()
                ),
                new CategoryResponse(
                        dto.categoryId(),
                        dto.categoryName()
                ),
                dto.viewCount(),
                dto.likeCount(),
                dto.createdAt()
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