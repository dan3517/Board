package com.example.board.domain.category.controller;

import com.example.board.domain.category.controller.docs.CategoryApiDocs;
import com.example.board.domain.category.dto.response.CategoryListResponse;
import com.example.board.domain.category.service.CategoryService;
import com.example.board.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController implements CategoryApiDocs {

    private final CategoryService categoryService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<CategoryListResponse>>
    getCategories() {
        CategoryListResponse response =
                categoryService.getActiveCategories();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "CATEGORY200",
                        "카테고리 목록을 조회했습니다.",
                        response
                )
        );
    }
}