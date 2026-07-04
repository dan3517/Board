package com.example.board.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(

        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(
                max = 1000,
                message = "댓글은 1,000자 이하여야 합니다."
        )
        String content
) {
}