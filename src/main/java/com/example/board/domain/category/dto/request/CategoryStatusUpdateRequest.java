package com.example.board.domain.category.dto.request;

import com.example.board.domain.category.entity.CategoryStatus;
import jakarta.validation.constraints.NotNull;

public record CategoryStatusUpdateRequest(

        @NotNull(
                message = "카테고리 상태는 필수입니다."
        )
        CategoryStatus status
) {
}