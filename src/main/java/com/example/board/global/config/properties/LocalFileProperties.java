package com.example.board.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.file.local")
public record LocalFileProperties(
        String rootPath,
        String publicBaseUrl
) {

    public LocalFileProperties {
        if (!StringUtils.hasText(rootPath)) {
            throw new IllegalArgumentException(
                    "app.file.local.root-path는 필수입니다."
            );
        }

        if (!StringUtils.hasText(publicBaseUrl)) {
            throw new IllegalArgumentException(
                    "app.file.local.public-base-url은 필수입니다."
            );
        }
    }
}