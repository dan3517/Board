package com.example.board.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        name = "SignUpRequest",
        description = "회원가입 요청"
)
public record SignupRequest(

        @Schema(
                description = "로그인에 사용할 이메일",
                example = "user@example.com"
        )
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
        String email,

        @Schema(
                description = "비밀번호",
                example = "password123!",
                writeOnly = true
        )
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(
                min = 8,
                max = 64,
                message = "비밀번호는 8자 이상 64자 이하여야 합니다."
        )
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s]).{8,64}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
        )
        String password,

        @Schema(
                description = "서비스에서 사용할 닉네임",
                example = "backend"
        )
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(
                min = 2,
                max = 20,
                message = "닉네임은 2자 이상 20자 이하여야 합니다."
        )
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9_]+$",
                message = "닉네임은 한글, 영문, 숫자, 밑줄만 사용할 수 있습니다."
        )
        String nickname
) {
}