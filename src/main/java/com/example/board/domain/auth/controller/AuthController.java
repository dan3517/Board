package com.example.board.domain.auth.controller;

import com.example.board.domain.auth.controller.docs.AuthApiDocs;
import com.example.board.domain.member.dto.request.LoginRequest;
import com.example.board.domain.member.dto.request.ReissueRequest;
import com.example.board.domain.member.dto.request.SignupRequest;
import com.example.board.domain.member.dto.response.SignupResponse;
import com.example.board.domain.member.dto.response.TokenResponse;
import com.example.board.domain.member.service.AuthService;
import com.example.board.global.common.response.ApiResponse;
import com.example.board.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApiDocs {

    private final AuthService authService;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>>
    signup(
            @Valid
            @RequestBody
            SignupRequest request
    ) {
        SignupResponse response =
                authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "MEMBER201",
                                "회원가입에 성공했습니다.",
                                response
                        )
                );
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>>
    login(
            @Valid
            @RequestBody
            LoginRequest request
    ) {
        TokenResponse response =
                authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "AUTH200",
                        "로그인에 성공했습니다.",
                        response
                )
        );
    }

    @Override
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>>
    reissue(
            @Valid
            @RequestBody
            ReissueRequest request
    ) {
        TokenResponse response =
                authService.reissue(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "AUTH2001",
                        "토큰을 재발급했습니다.",
                        response
                )
        );
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>>
    logout(
            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {
        authService.logout(
                userDetails.getMemberId()
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>success(
                        "AUTH2002",
                        "로그아웃했습니다.",
                        null
                )
        );
    }
}