package com.example.board.domain.comment.dto.response;

import com.example.board.domain.comment.dto.query.CommentListQueryDto;

import java.time.LocalDateTime;

public record CommentListItemResponse(
        Long commentId,
        String content,
        AuthorResponse author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CommentListItemResponse from(
            CommentListQueryDto dto
    ) {
        return new CommentListItemResponse(
                dto.commentId(),
                dto.content(),
                new AuthorResponse(
                        dto.authorId(),
                        dto.authorNickname()
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
}