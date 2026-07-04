package com.example.board.domain.category.controller;

import com.example.board.domain.category.dto.request.CategoryCreateRequest;
import com.example.board.domain.category.dto.request.CategoryStatusUpdateRequest;
import com.example.board.domain.category.dto.request.CategoryUpdateRequest;
import com.example.board.domain.category.dto.response.CategoryCreateResponse;
import com.example.board.domain.category.dto.response.CategoryResponse;
import com.example.board.domain.category.entity.CategoryStatus;
import com.example.board.domain.category.service.CategoryService;
import com.example.board.global.common.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AdminCategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private AdminCategoryController adminCategoryController;

    @Test
    @DisplayName("관리자 카테고리 생성 요청을 처리한다")
    void createCategorySuccess() {
        // given
        CategoryCreateRequest request =
                new CategoryCreateRequest("공지");

        CategoryCreateResponse serviceResponse =
                new CategoryCreateResponse(1L);

        given(
                categoryService.createCategory(request)
        ).willReturn(serviceResponse);

        // when
        ResponseEntity<ApiResponse<CategoryCreateResponse>>
                response =
                adminCategoryController.createCategory(
                        request
                );

        // then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);

        assertThat(response.getBody())
                .isNotNull();

        ApiResponse<CategoryCreateResponse> body =
                response.getBody();

        assertThat(body.isSuccess())
                .isTrue();

        assertThat(body.getCode())
                .isEqualTo("CATEGORY201");

        assertThat(body.getMessage())
                .isEqualTo("카테고리를 생성했습니다.");

        assertThat(body.getResult())
                .isNotNull();

        assertThat(body.getResult().categoryId())
                .isEqualTo(1L);

        then(categoryService)
                .should()
                .createCategory(request);
    }

    @Test
    @DisplayName("관리자 카테고리 이름 수정 요청을 처리한다")
    void updateCategorySuccess() {
        // given
        Long categoryId = 1L;

        CategoryUpdateRequest request =
                new CategoryUpdateRequest(
                        "필독 공지"
                );

        CategoryResponse serviceResponse =
                new CategoryResponse(
                        categoryId,
                        "필독 공지",
                        CategoryStatus.ACTIVE
                );

        given(
                categoryService.updateCategory(
                        categoryId,
                        request
                )
        ).willReturn(serviceResponse);

        // when
        ResponseEntity<ApiResponse<CategoryResponse>>
                response =
                adminCategoryController.updateCategory(
                        categoryId,
                        request
                );

        // then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isNotNull();

        ApiResponse<CategoryResponse> body =
                response.getBody();

        assertThat(body.isSuccess())
                .isTrue();

        assertThat(body.getCode())
                .isEqualTo("CATEGORY2001");

        assertThat(body.getMessage())
                .isEqualTo(
                        "카테고리 이름을 수정했습니다."
                );

        assertThat(body.getResult())
                .isNotNull();

        assertThat(body.getResult().categoryId())
                .isEqualTo(categoryId);

        assertThat(body.getResult().name())
                .isEqualTo("필독 공지");

        assertThat(body.getResult().status())
                .isEqualTo(CategoryStatus.ACTIVE);

        then(categoryService)
                .should()
                .updateCategory(
                        categoryId,
                        request
                );
    }

    @Test
    @DisplayName("관리자 카테고리 상태 수정 요청을 처리한다")
    void updateCategoryStatusSuccess() {
        // given
        Long categoryId = 1L;

        CategoryStatusUpdateRequest request =
                new CategoryStatusUpdateRequest(
                        CategoryStatus.INACTIVE
                );

        CategoryResponse serviceResponse =
                new CategoryResponse(
                        categoryId,
                        "공지",
                        CategoryStatus.INACTIVE
                );

        given(
                categoryService.updateCategoryStatus(
                        categoryId,
                        request
                )
        ).willReturn(serviceResponse);

        // when
        ResponseEntity<ApiResponse<CategoryResponse>>
                response =
                adminCategoryController
                        .updateCategoryStatus(
                                categoryId,
                                request
                        );

        // then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isNotNull();

        ApiResponse<CategoryResponse> body =
                response.getBody();

        assertThat(body.isSuccess())
                .isTrue();

        assertThat(body.getCode())
                .isEqualTo("CATEGORY2002");

        assertThat(body.getMessage())
                .isEqualTo(
                        "카테고리 상태를 수정했습니다."
                );

        assertThat(body.getResult())
                .isNotNull();

        assertThat(body.getResult().categoryId())
                .isEqualTo(categoryId);

        assertThat(body.getResult().status())
                .isEqualTo(
                        CategoryStatus.INACTIVE
                );

        then(categoryService)
                .should()
                .updateCategoryStatus(
                        categoryId,
                        request
                );
    }
}