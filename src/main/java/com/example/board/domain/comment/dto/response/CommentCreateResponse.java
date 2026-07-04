package com.example.board.domain.comment.dto.response;

import com.example.board.domain.comment.entity.Comment;

public record CommentCreateResponse(
        Long commentId
) {

    public static CommentCreateResponse from(
            Comment comment
    ) {
        return new CommentCreateResponse(
                comment.getId()
        );
    }
}