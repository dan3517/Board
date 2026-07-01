package com.example.board.domain.category.dto.response;

import com.example.board.domain.category.entity.Category;

public record CategoryCreateResponse(
        Long categoryId
) {

    public static CategoryCreateResponse from(
            Category category
    ) {
        return new CategoryCreateResponse(
                category.getId()
        );
    }
}