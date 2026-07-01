package com.example.board.global.security;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class RefreshTokenHasher {

    private static final String HASH_ALGORITHM =
            "SHA-256";

    public String hash(String token) {
        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance(
                            HASH_ALGORITHM
                    );

            byte[] hashedBytes =
                    messageDigest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat
                    .of()
                    .formatHex(hashedBytes);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 알고리즘을 사용할 수 없습니다.",
                    exception
            );
        }
    }

    public boolean matches(
            String rawToken,
            String storedHash
    ) {
        byte[] actualHash = hash(rawToken)
                .getBytes(StandardCharsets.US_ASCII);

        byte[] expectedHash = storedHash
                .getBytes(StandardCharsets.US_ASCII);

        return MessageDigest.isEqual(
                actualHash,
                expectedHash
        );
    }
}