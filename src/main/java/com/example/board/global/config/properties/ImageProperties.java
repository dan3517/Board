package com.example.board.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.image")
public record ImageProperties(
        int maxCount,
        long maxSizeBytes
) {

    public ImageProperties {
        if (maxCount <= 0) {
            throw new IllegalArgumentException(
                    "app.image.max-count는 1 이상이어야 합니다."
            );
        }

        if (maxSizeBytes <= 0) {
            throw new IllegalArgumentException(
                    "app.image.max-size-bytes는 1 이상이어야 합니다."
            );
        }
    }
}