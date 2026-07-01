package com.example.board.domain.category.dto.response;

import com.example.board.domain.category.entity.Category;
import com.example.board.domain.category.entity.CategoryStatus;

public record CategoryResponse(
        Long categoryId,
        String name,
        CategoryStatus status
) {

    public static CategoryResponse from(
            Category category
    ) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getStatus()
        );
    }
}