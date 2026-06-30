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

@Component
public class JwtTokenProvider {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String ACCESS_TOKEN_TYPE = "access";

    private final SecretKey secretKey;
    private final JwtParser jwtParser;
    private final long accessTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms}")
            long accessTokenExpirationMs
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
    }

    public IssuedToken issueAccessToken(Long memberId) {
        Instant issuedAt = Instant.now();

        Instant expiresAt = issuedAt.plusMillis(
                accessTokenExpirationMs
        );

        String token = Jwts.builder()
                .subject(memberId.toString())
                .claim(
                        TOKEN_TYPE_CLAIM,
                        ACCESS_TOKEN_TYPE
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

    public Long extractMemberId(String token) {
        try {
            Claims claims = jwtParser
                    .parseSignedClaims(token)
                    .getPayload();

            validateAccessTokenType(claims);

            return Long.valueOf(
                    claims.getSubject()
            );

        } catch (ExpiredJwtException exception) {
            throw new JwtAuthenticationException(
                    ErrorCode.EXPIRED_TOKEN,
                    exception
            );

        } catch (
                JwtException |
                IllegalArgumentException exception
        ) {
            throw new JwtAuthenticationException(
                    ErrorCode.INVALID_TOKEN,
                    exception
            );
        }
    }

    private void validateAccessTokenType(
            Claims claims
    ) {
        String tokenType = claims.get(
                TOKEN_TYPE_CLAIM,
                String.class
        );

        if (!ACCESS_TOKEN_TYPE.equals(tokenType)) {
            throw new JwtAuthenticationException(
                    ErrorCode.INVALID_TOKEN
            );
        }
    }

    public record IssuedToken(
            String value,
            Instant expiresAt
    ) {
    }
}