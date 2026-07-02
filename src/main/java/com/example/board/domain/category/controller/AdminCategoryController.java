package com.example.board.domain.category.controller;

import com.example.board.domain.category.controller.docs.AdminCategoryApiDocs;
import com.example.board.domain.category.dto.request.CategoryCreateRequest;
import com.example.board.domain.category.dto.request.CategoryStatusUpdateRequest;
import com.example.board.domain.category.dto.request.CategoryUpdateRequest;
import com.example.board.domain.category.dto.response.CategoryCreateResponse;
import com.example.board.domain.category.dto.response.CategoryResponse;
import com.example.board.domain.category.service.CategoryService;
import com.example.board.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController implements AdminCategoryApiDocs {

    private final CategoryService categoryService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryCreateResponse>>
    createCategory(
            @Valid
            @RequestBody
            CategoryCreateRequest request
    ) {
        CategoryCreateResponse response =
                categoryService.createCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "CATEGORY201",
                                "카테고리를 생성했습니다.",
                                response
                        )
                );
    }

    @Override
    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>>
    updateCategory(
            @PathVariable
            Long categoryId,

            @Valid
            @RequestBody
            CategoryUpdateRequest request
    ) {
        CategoryResponse response =
                categoryService.updateCategory(
                        categoryId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "CATEGORY2001",
                        "카테고리 이름을 수정했습니다.",
                        response
                )
        );
    }

    @Override
    @PatchMapping("/{categoryId}/status")
    public ResponseEntity<ApiResponse<CategoryResponse>>
    updateCategoryStatus(
            @PathVariable
            Long categoryId,

            @Valid
            @RequestBody
            CategoryStatusUpdateRequest request
    ) {
        CategoryResponse response =
                categoryService.updateCategoryStatus(
                        categoryId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "CATEGORY2002",
                        "카테고리 상태를 수정했습니다.",
                        response
                )
        );
    }
}