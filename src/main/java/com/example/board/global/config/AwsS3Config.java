package com.example.board.global.config;

import com.example.board.global.config.properties.S3FileProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
        S3FileProperties.class
)
@ConditionalOnProperty(
        prefix = "app.file",
        name = "storage",
        havingValue = "s3"
)
public class AwsS3Config {

    @Bean(destroyMethod = "close")
    public S3Client s3Client(
            S3FileProperties properties
    ) {
        return S3Client.builder()
                .region(
                        Region.of(properties.region())
                )
                .credentialsProvider(
                        DefaultCredentialsProvider.create()
                )
                .build();
    }

    @Bean(destroyMethod = "close")
    public S3Presigner s3Presigner(
            S3FileProperties properties
    ) {
        return S3Presigner.builder()
                .region(
                        Region.of(properties.region())
                )
                .credentialsProvider(
                        DefaultCredentialsProvider.create()
                )
                .build();
    }
}