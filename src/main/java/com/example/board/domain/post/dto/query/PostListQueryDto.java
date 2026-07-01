package com.example.board.domain.post.dto.query;

import java.time.LocalDateTime;

public record PostListQueryDto(
        Long postId,
        String title,
        Long authorId,
        String authorNickname,
        Long categoryId,
        String categoryName,
        long viewCount,
        long likeCount,
        LocalDateTime createdAt
) {
}