package com.example.board.domain.post.dto.query;

import java.time.LocalDateTime;

public record PostDetailQueryDto(
        Long postId,
        String title,
        String content,
        long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        Long authorId,
        String authorNickname,

        Long categoryId,
        String categoryName
) {
}