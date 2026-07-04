package com.example.board.domain.category.dto.request;

import com.example.board.domain.category.entity.CategoryStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory =
                Validation
                        .buildDefaultValidatorFactory();

        validator =
                validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("카테고리 이름이 공백이면 검증에 실패한다")
    void createRequestFailsWhenNameBlank() {
        // given
        CategoryCreateRequest request =
                new CategoryCreateRequest(" ");

        // when
        Set<ConstraintViolation<CategoryCreateRequest>>
                violations =
                validator.validate(request);

        // then
        assertThat(violations)
                .hasSize(1);

        assertThat(
                violations.iterator()
                        .next()
                        .getMessage()
        ).isEqualTo(
                "카테고리 이름은 필수입니다."
        );
    }

    @Test
    @DisplayName("카테고리 이름이 50자를 초과하면 검증에 실패한다")
    void createRequestFailsWhenNameTooLong() {
        // given
        String longName = "가".repeat(51);

        CategoryCreateRequest request =
                new CategoryCreateRequest(longName);

        // when
        Set<ConstraintViolation<CategoryCreateRequest>>
                violations =
                validator.validate(request);

        // then
        assertThat(violations)
                .hasSize(1);

        assertThat(
                violations.iterator()
                        .next()
                        .getMessage()
        ).isEqualTo(
                "카테고리 이름은 50자 이하여야 합니다."
        );
    }

    @Test
    @DisplayName("카테고리 상태가 null이면 검증에 실패한다")
    void statusRequestFailsWhenStatusNull() {
        // given
        CategoryStatusUpdateRequest request =
                new CategoryStatusUpdateRequest(null);

        // when
        Set<ConstraintViolation<CategoryStatusUpdateRequest>>
                violations =
                validator.validate(request);

        // then
        assertThat(violations)
                .hasSize(1);

        assertThat(
                violations.iterator()
                        .next()
                        .getMessage()
        ).isEqualTo(
                "카테고리 상태는 필수입니다."
        );
    }

    @Test
    @DisplayName("정상적인 카테고리 요청은 검증을 통과한다")
    void validRequestSuccess() {
        // given
        CategoryCreateRequest createRequest =
                new CategoryCreateRequest("공지");

        CategoryStatusUpdateRequest statusRequest =
                new CategoryStatusUpdateRequest(
                        CategoryStatus.ACTIVE
                );

        // when
        Set<ConstraintViolation<CategoryCreateRequest>>
                createViolations =
                validator.validate(createRequest);

        Set<ConstraintViolation<CategoryStatusUpdateRequest>>
                statusViolations =
                validator.validate(statusRequest);

        // then
        assertThat(createViolations)
                .isEmpty();

        assertThat(statusViolations)
                .isEmpty();
    }
}