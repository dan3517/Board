package com.example.board.global.swagger.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ApiErrorResponse",
        description = "공통 API 오류 응답"
)
public record ApiErrorResponse(

        @Schema(
                description = "요청 성공 여부",
                example = "false"
        )
        boolean success,

        @Schema(
                description = "애플리케이션 오류 코드",
                example = "POST404"
        )
        String code,

        @Schema(
                description = "오류 메시지",
                example = "게시글을 찾을 수 없습니다."
        )
        String message,

        @Schema(
                description = "오류 상세 데이터",
                nullable = true
        )
        Object result
) {
}