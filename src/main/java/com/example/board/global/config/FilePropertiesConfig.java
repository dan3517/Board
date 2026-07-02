package com.example.board.global.config;

import com.example.board.global.config.properties.ImageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
        ImageProperties.class
)
public class FilePropertiesConfig {
}