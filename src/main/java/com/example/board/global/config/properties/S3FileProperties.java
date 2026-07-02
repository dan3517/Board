package com.example.board.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.file.s3")
public record S3FileProperties(
        String bucket,
        String region,
        long presignedUrlDurationMinutes
) {

    public S3FileProperties {
        if (!StringUtils.hasText(bucket)) {
            throw new IllegalArgumentException(
                    "app.file.s3.bucket은 필수입니다."
            );
        }

        if (!StringUtils.hasText(region)) {
            throw new IllegalArgumentException(
                    "app.file.s3.region은 필수입니다."
            );
        }

        if (presignedUrlDurationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "app.file.s3.presigned-url-duration-minutes는 "
                            + "1 이상이어야 합니다."
            );
        }

        if (presignedUrlDurationMinutes > 10080) {
            throw new IllegalArgumentException(
                    "S3 presigned URL 유효 시간은 "
                            + "7일 이하여야 합니다."
            );
        }
    }

    public Duration presignedUrlDuration() {
        return Duration.ofMinutes(
                presignedUrlDurationMinutes
        );
    }
}