package com.example.board.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "LoginRequest",
        description = "로그인 요청"
)
public record LoginRequest(

        @Schema(
                description = "회원 이메일",
                example = "user@example.com"
        )
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(
                max = 255,
                message = "이메일은 255자 이하여야 합니다."
        )
        String email,

        @Schema(
                description = "회원 비밀번호",
                example = "password123!",
                writeOnly = true
        )
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(
                max = 64,
                message = "비밀번호는 64자 이하여야 합니다."
        )
        String password
) {
}