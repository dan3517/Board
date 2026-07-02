package com.example.board.global.config;

import com.example.board.global.config.properties.ImageProperties;
import com.example.board.global.config.properties.LocalFileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        ImageProperties.class,
        LocalFileProperties.class
})
public class FilePropertiesConfig {
}