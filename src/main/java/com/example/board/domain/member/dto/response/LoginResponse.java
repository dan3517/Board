package com.example.board.domain.member.dto.response;

import com.example.board.global.security.JwtTokenProvider;

import java.time.Instant;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt
) {

    public static LoginResponse from(
            JwtTokenProvider.IssuedToken issuedToken
    ) {
        return new LoginResponse(
                issuedToken.value(),
                "Bearer",
                issuedToken.expiresAt()
        );
    }
}