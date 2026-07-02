package com.example.board.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(
        name = "PostCreateRequest",
        description = "게시글 작성 요청"
)
public record PostCreateRequest(

        @Schema(
                description = "카테고리 ID",
                example = "4"
        )
        @NotNull(message = "카테고리는 필수입니다.")
        @Positive(message = "카테고리 ID는 양수여야 합니다.")
        Long categoryId,

        @Schema(
                description = "게시글 제목",
                example = "Spring Boot 4 게시판 만들기",
                maxLength = 100
        )
        @NotBlank(message = "게시글 제목은 필수입니다.")
        @Size(
                max = 100,
                message = "게시글 제목은 100자 이하여야 합니다."
        )
        String title,

        @Schema(
                description = "게시글 본문",
                example = "오늘은 게시판 API를 구현했습니다.",
                maxLength = 10000
        )
        @NotBlank(message = "게시글 내용은 필수입니다.")
        @Size(
                max = 10000,
                message = "게시글 내용은 10,000자 이하여야 합니다."
        )
        String content
) {
}