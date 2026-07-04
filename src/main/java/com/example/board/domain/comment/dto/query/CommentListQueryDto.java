package com.example.board.domain.comment.dto.query;

import java.time.LocalDateTime;

public record CommentListQueryDto(
        Long commentId,
        String content,
        Long authorId,
        String authorNickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}