package com.example.board.domain.post.dto.response;

import com.example.board.domain.post.dto.query.PostListQueryDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record PostListResponse(
        List<PostListItemResponse> posts,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {

    public static PostListResponse from(
            Page<PostListQueryDto> result
    ) {
        List<PostListItemResponse> posts =
                result.getContent()
                        .stream()
                        .map(PostListItemResponse::from)
                        .toList();

        return new PostListResponse(
                posts,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext(),
                result.hasPrevious()
        );
    }
}