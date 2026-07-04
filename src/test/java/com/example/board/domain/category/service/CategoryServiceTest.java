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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService =
                new CategoryService(
                        categoryRepository
                );
    }

    @Test
    @DisplayName("활성 카테고리 목록을 ID 오름차순으로 조회한다")
    void getActiveCategoriesSuccess() {
        // given
        Category free = createCategory(
                1L,
                "자유"
        );

        Category study = createCategory(
                2L,
                "공부 기록"
        );

        given(
                categoryRepository
                        .findAllByStatusOrderByIdAsc(
                                CategoryStatus.ACTIVE
                        )
        ).willReturn(
                List.of(free, study)
        );

        // when
        CategoryListResponse response =
                categoryService
                        .getActiveCategories();

        // then
        assertThat(response.categories())
                .hasSize(2);

        assertThat(
                response.categories()
                        .getFirst()
                        .categoryId()
        ).isEqualTo(1L);

        assertThat(
                response.categories()
                        .getFirst()
                        .name()
        ).isEqualTo("자유");

        assertThat(
                response.categories()
                        .get(1)
                        .name()
        ).isEqualTo("공부 기록");
    }

    @Test
    @DisplayName("관리자가 카테고리를 생성한다")
    void createCategorySuccess() {
        // given
        CategoryCreateRequest request =
                new CategoryCreateRequest(
                        "  공부   기록  "
                );

        given(
                categoryRepository
                        .existsByNameIgnoreCase(
                                "공부 기록"
                        )
        ).willReturn(false);

        given(
                categoryRepository.save(
                        any(Category.class)
                )
        ).willAnswer(invocation -> {
            Category category =
                    invocation.getArgument(0);

            ReflectionTestUtils.setField(
                    category,
                    "id",
                    10L
            );

            return category;
        });

        // when
        CategoryCreateResponse response =
                categoryService
                        .createCategory(request);

        // then
        assertThat(response.categoryId())
                .isEqualTo(10L);

        ArgumentCaptor<Category> captor =
                ArgumentCaptor.forClass(
                        Category.class
                );

        then(categoryRepository)
                .should()
                .save(captor.capture());

        Category savedCategory =
                captor.getValue();

        assertThat(savedCategory.getName())
                .isEqualTo("공부 기록");

        assertThat(savedCategory.getStatus())
                .isEqualTo(
                        CategoryStatus.ACTIVE
                );
    }

    @Test
    @DisplayName("카테고리 이름이 중복되면 생성에 실패한다")
    void createCategoryFailsWhenNameDuplicated() {
        // given
        CategoryCreateRequest request =
                new CategoryCreateRequest("자유");

        given(
                categoryRepository
                        .existsByNameIgnoreCase(
                                "자유"
                        )
        ).willReturn(true);

        // when
        Throwable throwable =
                catchThrowable(
                        () -> categoryService
                                .createCategory(request)
                );

        // then
        assertThat(throwable)
                .isInstanceOf(
                        BusinessException.class
                );

        BusinessException exception =
                (BusinessException) throwable;

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode
                                .DUPLICATE_CATEGORY_NAME
                );

        then(categoryRepository)
                .should(never())
                .save(any(Category.class));
    }

    @Test
    @DisplayName("관리자가 카테고리 이름을 수정한다")
    void updateCategorySuccess() {
        // given
        Long categoryId = 1L;

        Category category =
                createCategory(
                        categoryId,
                        "기존 이름"
                );

        CategoryUpdateRequest request =
                new CategoryUpdateRequest(
                        "  변경된   이름  "
                );

        given(
                categoryRepository.findById(
                        categoryId
                )
        ).willReturn(
                Optional.of(category)
        );

        given(
                categoryRepository
                        .existsByNameIgnoreCaseAndIdNot(
                                "변경된 이름",
                                categoryId
                        )
        ).willReturn(false);

        // when
        CategoryResponse response =
                categoryService.updateCategory(
                        categoryId,
                        request
                );

        // then
        assertThat(response.categoryId())
                .isEqualTo(categoryId);

        assertThat(response.name())
                .isEqualTo("변경된 이름");

        assertThat(category.getName())
                .isEqualTo("변경된 이름");

        then(categoryRepository)
                .should(never())
                .save(any(Category.class));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리는 수정할 수 없다")
    void updateCategoryFailsWhenNotFound() {
        // given
        Long categoryId = 999L;

        CategoryUpdateRequest request =
                new CategoryUpdateRequest(
                        "수정 이름"
                );

        given(
                categoryRepository.findById(
                        categoryId
                )
        ).willReturn(Optional.empty());

        // when
        Throwable throwable =
                catchThrowable(
                        () -> categoryService
                                .updateCategory(
                                        categoryId,
                                        request
                                )
                );

        // then
        assertThat(throwable)
                .isInstanceOf(
                        BusinessException.class
                );

        BusinessException exception =
                (BusinessException) throwable;

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.CATEGORY_NOT_FOUND
                );

        then(categoryRepository)
                .should(never())
                .existsByNameIgnoreCaseAndIdNot(
                        any(),
                        any()
                );
    }

    @Test
    @DisplayName("관리자가 카테고리를 비활성화한다")
    void deactivateCategorySuccess() {
        // given
        Long categoryId = 1L;

        Category category =
                createCategory(
                        categoryId,
                        "자유"
                );

        CategoryStatusUpdateRequest request =
                new CategoryStatusUpdateRequest(
                        CategoryStatus.INACTIVE
                );

        given(
                categoryRepository.findById(
                        categoryId
                )
        ).willReturn(
                Optional.of(category)
        );

        // when
        CategoryResponse response =
                categoryService
                        .updateCategoryStatus(
                                categoryId,
                                request
                        );

        // then
        assertThat(response.status())
                .isEqualTo(
                        CategoryStatus.INACTIVE
                );

        assertThat(category.getStatus())
                .isEqualTo(
                        CategoryStatus.INACTIVE
                );

        then(categoryRepository)
                .should(never())
                .save(any(Category.class));
    }

    private Category createCategory(
            Long id,
            String name
    ) {
        Category category =
                Category.create(name);

        ReflectionTestUtils.setField(
                category,
                "id",
                id
        );

        return category;
    }
}