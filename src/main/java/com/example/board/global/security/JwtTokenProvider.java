package com.example.board.global.security;

import com.example.board.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final String TOKEN_TYPE_CLAIM =
            "type";

    private static final String ACCESS_TOKEN_TYPE =
            "access";

    private static final String REFRESH_TOKEN_TYPE =
            "refresh";

    private final SecretKey secretKey;
    private final JwtParser jwtParser;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}")
            String secret,

            @Value("${jwt.access-token-expiration-ms}")
            long accessTokenExpirationMs,

            @Value("${jwt.refresh-token-expiration-ms}")
            long refreshTokenExpirationMs
    ) {
        byte[] keyBytes =
                Decoders.BASE64.decode(secret);

        this.secretKey =
                Keys.hmacShaKeyFor(keyBytes);

        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();

        this.accessTokenExpirationMs =
                accessTokenExpirationMs;

        this.refreshTokenExpirationMs =
                refreshTokenExpirationMs;
    }

    public TokenPair issueTokenPair(Long memberId) {
        Instant issuedAt = Instant.now();

        IssuedToken accessToken = issueToken(
                memberId,
                ACCESS_TOKEN_TYPE,
                issuedAt,
                accessTokenExpirationMs
        );

        IssuedToken refreshToken = issueToken(
                memberId,
                REFRESH_TOKEN_TYPE,
                issuedAt,
                refreshTokenExpirationMs
        );

        return new TokenPair(
                accessToken,
                refreshToken
        );
    }

    public Long extractAccessTokenMemberId(
            String token
    ) {
        Claims claims = parseClaims(
                token,
                ACCESS_TOKEN_TYPE,
                ErrorCode.EXPIRED_TOKEN,
                ErrorCode.INVALID_TOKEN
        );

        return parseMemberId(
                claims,
                ErrorCode.INVALID_TOKEN
        );
    }

    public Long extractRefreshTokenMemberId(
            String token
    ) {
        Claims claims = parseClaims(
                token,
                REFRESH_TOKEN_TYPE,
                ErrorCode.EXPIRED_REFRESH_TOKEN,
                ErrorCode.INVALID_REFRESH_TOKEN
        );

        return parseMemberId(
                claims,
                ErrorCode.INVALID_REFRESH_TOKEN
        );
    }

    private IssuedToken issueToken(
            Long memberId,
            String tokenType,
            Instant issuedAt,
            long expirationMs
    ) {
        Instant expiresAt =
                issuedAt.plusMillis(expirationMs);

        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(memberId.toString())
                .claim(
                        TOKEN_TYPE_CLAIM,
                        tokenType
                )
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();

        return new IssuedToken(
                token,
                expiresAt
        );
    }

    private Claims parseClaims(
            String token,
            String expectedTokenType,
            ErrorCode expiredErrorCode,
            ErrorCode invalidErrorCode
    ) {
        try {
            Claims claims = jwtParser
                    .parseSignedClaims(token)
                    .getPayload();

            validateTokenType(
                    claims,
                    expectedTokenType,
                    invalidErrorCode
            );

            return claims;

        } catch (ExpiredJwtException exception) {
            throw new JwtAuthenticationException(
                    expiredErrorCode,
                    exception
            );

        } catch (JwtAuthenticationException exception) {
            throw exception;

        } catch (
                JwtException |
                IllegalArgumentException exception
        ) {
            throw new JwtAuthenticationException(
                    invalidErrorCode,
                    exception
            );
        }
    }

    private void validateTokenType(
            Claims claims,
            String expectedTokenType,
            ErrorCode errorCode
    ) {
        String tokenType = claims.get(
                TOKEN_TYPE_CLAIM,
                String.class
        );

        if (!expectedTokenType.equals(tokenType)) {
            throw new JwtAuthenticationException(
                    errorCode
            );
        }
    }

    private Long parseMemberId(
            Claims claims,
            ErrorCode errorCode
    ) {
        try {
            return Long.valueOf(
                    claims.getSubject()
            );

        } catch (
                NumberFormatException |
                NullPointerException exception
        ) {
            throw new JwtAuthenticationException(
                    errorCode,
                    exception
            );
        }
    }

    public record IssuedToken(
            String value,
            Instant expiresAt
    ) {
    }

    public record TokenPair(
            IssuedToken accessToken,
            IssuedToken refreshToken
    ) {
    }
}