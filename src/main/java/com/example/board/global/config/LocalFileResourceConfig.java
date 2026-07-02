package com.example.board.global.config;

import com.example.board.global.config.properties.LocalFileProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "app.file",
        name = "storage",
        havingValue = "local",
        matchIfMissing = true
)
public class LocalFileResourceConfig
        implements WebMvcConfigurer {

    private final LocalFileProperties properties;

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {
        Path rootPath =
                Path.of(properties.rootPath())
                        .toAbsolutePath()
                        .normalize();

        String resourceLocation =
                rootPath.toUri().toString();

        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }

        registry
                .addResourceHandler("/files/**")
                .addResourceLocations(
                        resourceLocation
                );
    }
}