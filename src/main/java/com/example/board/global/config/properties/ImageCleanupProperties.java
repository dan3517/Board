package com.example.board.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "app.image.cleanup"
)
public record ImageCleanupProperties(
        int batchSize
) {

    private static final int MAX_S3_DELETE_SIZE = 1000;

    public ImageCleanupProperties {
        if (batchSize <= 0) {
            throw new IllegalArgumentException(
                    "app.image.cleanup.batch-size는 "
                            + "1 이상이어야 합니다."
            );
        }

        if (batchSize > MAX_S3_DELETE_SIZE) {
            throw new IllegalArgumentException(
                    "app.image.cleanup.batch-size는 "
                            + "1,000 이하여야 합니다."
            );
        }
    }
}