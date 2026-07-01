package com.example.board.domain.post.dto.response;

import com.example.board.domain.post.entity.Post;

public record PostUpdateResponse(
        Long postId
) {

    public static PostUpdateResponse from(Post post) {
        return new PostUpdateResponse(
                post.getId()
        );
    }
}