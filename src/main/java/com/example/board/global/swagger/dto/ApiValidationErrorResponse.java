package com.example.board.global.swagger.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(
        name = "ApiValidationErrorResponse",
        description = "요청 필드 검증 실패 응답"
)
public record ApiValidationErrorResponse(

        @Schema(
                description = "요청 성공 여부",
                example = "false"
        )
        boolean success,

        @Schema(
                description = "애플리케이션 오류 코드",
                example = "COMMON400"
        )
        String code,

        @Schema(
                description = "오류 메시지",
                example = "입력값이 올바르지 않습니다."
        )
        String message,

        @Schema(
                description = "필드별 오류 메시지"
        )
        Map<String, String> result
) {
}