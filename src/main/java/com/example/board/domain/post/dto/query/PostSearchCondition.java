package com.example.board.domain.post.dto.query;

import com.example.board.domain.post.dto.request.PostSortType;

public record PostSearchCondition(
        String keyword,
        String author,
        Long categoryId,
        PostSortType sort
) {
}