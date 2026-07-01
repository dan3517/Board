package com.example.board.domain.postlike.dto.response;

public record PostLikeResponse(
        Long postId,
        boolean liked,
        long likeCount
) {

    public static PostLikeResponse liked(
            Long postId,
            long likeCount
    ) {
        return new PostLikeResponse(
                postId,
                true,
                likeCount
        );
    }

    public static PostLikeResponse unliked(
            Long postId,
            long likeCount
    ) {
        return new PostLikeResponse(
                postId,
                false,
                likeCount
        );
    }
}