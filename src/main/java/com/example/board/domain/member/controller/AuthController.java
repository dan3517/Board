package com.example.board.domain.member.controller;

import com.example.board.domain.member.dto.request.SignupRequest;
import com.example.board.domain.member.dto.response.SignupResponse;
import com.example.board.domain.member.service.AuthService;
import com.example.board.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = authService.signup(request);

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
}