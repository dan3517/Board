package com.example.board.domain.category.dto.response;

import com.example.board.domain.category.entity.Category;

import java.util.List;

public record CategoryListResponse(
        List<CategoryResponse> categories
) {

    public static CategoryListResponse from(
            List<Category> categories
    ) {
        List<CategoryResponse> responses =
                categories.stream()
                        .map(CategoryResponse::from)
                        .toList();

        return new CategoryListResponse(responses);
    }
}