package com.example.board.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(

        @NotNull(message = "카테고리는 필수입니다.")
        @Positive(message = "카테고리 ID는 양수여야 합니다.")
        Long categoryId,

        @NotBlank(message = "게시글 제목은 필수입니다.")
        @Size(
                max = 100,
                message = "게시글 제목은 100자 이하여야 합니다."
        )
        String title,

        @NotBlank(message = "게시글 내용은 필수입니다.")
        @Size(
                max = 10000,
                message = "게시글 내용은 10,000자 이하여야 합니다."
        )
        String content
) {
}