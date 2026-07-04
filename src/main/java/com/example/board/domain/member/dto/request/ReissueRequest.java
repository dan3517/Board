package com.example.board.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "ReissueRequest",
        description = "Access Token 재발급 요청"
)
public record ReissueRequest(

        @Schema(
                description = "로그인 시 발급받은 Refresh Token",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        @NotBlank(
                message = "리프레시 토큰은 필수입니다."
        )
        String refreshToken
) {
}