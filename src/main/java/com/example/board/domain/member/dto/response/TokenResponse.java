package com.example.board.domain.member.dto.response;

import com.example.board.global.security.JwtTokenProvider;

import java.time.Instant;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt
) {

    public static TokenResponse from(
            JwtTokenProvider.TokenPair tokenPair
    ) {
        return new TokenResponse(
                tokenPair.accessToken().value(),
                tokenPair.refreshToken().value(),
                "Bearer",
                tokenPair.accessToken().expiresAt(),
                tokenPair.refreshToken().expiresAt()
        );
    }
}