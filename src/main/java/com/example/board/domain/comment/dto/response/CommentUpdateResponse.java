package com.example.board.domain.comment.dto.response;

import com.example.board.domain.comment.entity.Comment;

public record CommentUpdateResponse(
        Long commentId
) {

    public static CommentUpdateResponse from(
            Comment comment
    ) {
        return new CommentUpdateResponse(
                comment.getId()
        );
    }
}