package com.example.board.domain.post.dto.response;

import com.example.board.domain.image.dto.response.PostImageResponse;
import com.example.board.domain.post.dto.query.PostDetailQueryDto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
        Long postId,
        String title,
        String content,
        long viewCount,
        long commentCount,
        long likeCount,
        boolean likedByMe,
        List<PostImageResponse> images,
        AuthorResponse author,
        CategoryResponse category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PostDetailResponse from(
            PostDetailQueryDto dto,
            long commentCount,
            long likeCount,
            boolean likedByMe,
            List<PostImageResponse> images
    ) {
        return new PostDetailResponse(
                dto.postId(),
                dto.title(),
                dto.content(),
                dto.viewCount(),
                commentCount,
                likeCount,
                likedByMe,
                images,
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