package com.example.board.domain.category.service;

import com.example.board.domain.category.dto.request.CategoryCreateRequest;
import com.example.board.domain.category.dto.request.CategoryStatusUpdateRequest;
import com.example.board.domain.category.dto.request.CategoryUpdateRequest;
import com.example.board.domain.category.dto.response.CategoryCreateResponse;
import com.example.board.domain.category.dto.response.CategoryListResponse;
import com.example.board.domain.category.dto.response.CategoryResponse;
import com.example.board.domain.category.entity.Category;
import com.example.board.domain.category.entity.CategoryStatus;
import com.example.board.domain.category.repository.CategoryRepository;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryListResponse getActiveCategories() {
        return CategoryListResponse.from(
                categoryRepository
                        .findAllByStatusOrderByIdAsc(
                                CategoryStatus.ACTIVE
                        )
        );
    }

    @Transactional
    public CategoryCreateResponse createCategory(
            CategoryCreateRequest request
    ) {
        String normalizedName =
                normalizeName(request.name());

        validateDuplicateName(normalizedName);

        Category category =
                Category.create(normalizedName);

        Category savedCategory =
                categoryRepository.save(category);

        return CategoryCreateResponse.from(
                savedCategory
        );
    }

    @Transactional
    public CategoryResponse updateCategory(
            Long categoryId,
            CategoryUpdateRequest request
    ) {
        Category category =
                findCategory(categoryId);

        String normalizedName =
                normalizeName(request.name());

        validateDuplicateNameExceptSelf(
                normalizedName,
                categoryId
        );

        category.changeName(normalizedName);

        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse updateCategoryStatus(
            Long categoryId,
            CategoryStatusUpdateRequest request
    ) {
        Category category =
                findCategory(categoryId);

        switch (request.status()) {
            case ACTIVE -> category.activate();
            case INACTIVE -> category.deactivate();
        }

        return CategoryResponse.from(category);
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.CATEGORY_NOT_FOUND
                        )
                );
    }

    private void validateDuplicateName(
            String name
    ) {
        if (categoryRepository
                .existsByNameIgnoreCase(name)) {

            throw new BusinessException(
                    ErrorCode.DUPLICATE_CATEGORY_NAME
            );
        }
    }

    private void validateDuplicateNameExceptSelf(
            String name,
            Long categoryId
    ) {
        if (categoryRepository
                .existsByNameIgnoreCaseAndIdNot(
                        name,
                        categoryId
                )) {

            throw new BusinessException(
                    ErrorCode.DUPLICATE_CATEGORY_NAME
            );
        }
    }

    private String normalizeName(String name) {
        return name
                .strip()
                .replaceAll("\\s+", " ");
    }
}