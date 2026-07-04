package com.example.board.domain.comment.dto.response;

import com.example.board.domain.comment.dto.query.CommentListQueryDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record CommentListResponse(
        List<CommentListItemResponse> comments,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {

    public static CommentListResponse from(
            Page<CommentListQueryDto> result
    ) {
        List<CommentListItemResponse> comments =
                result.getContent()
                        .stream()
                        .map(CommentListItemResponse::from)
                        .toList();

        return new CommentListResponse(
                comments,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext(),
                result.hasPrevious()
        );
    }
}