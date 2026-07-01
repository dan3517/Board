package com.example.board.domain.post.dto.response;

import com.example.board.domain.post.entity.Post;

public record PostCreateResponse(
        Long postId
) {

    public static PostCreateResponse from(Post post) {
        return new PostCreateResponse(
                post.getId()
        );
    }
}