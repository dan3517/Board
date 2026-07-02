package com.example.board.domain.image.storage.s3;

import com.example.board.domain.image.storage.ImageStorage;
import com.example.board.domain.image.validation.ImageType;
import com.example.board.global.config.properties.S3FileProperties;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "app.file",
        name = "storage",
        havingValue = "s3"
)
public class S3ImageStorage
        implements ImageStorage {

    private static final String POST_IMAGE_PREFIX =
            "posts";

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3FileProperties properties;

    @Override
    public String store(
            Long postId,
            MultipartFile file,
            ImageType imageType
    ) {
        String storageKey =
                createStorageKey(
                        postId,
                        imageType
                );

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucket(properties.bucket())
                        .key(storageKey)
                        .contentType(
                                imageType.getContentType()
                        )
                        .contentLength(file.getSize())
                        .build();

        try (
                InputStream inputStream =
                        file.getInputStream()
        ) {
            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            inputStream,
                            file.getSize()
                    )
            );

            return storageKey;

        } catch (IOException | SdkException exception) {
            throw new BusinessException(
                    ErrorCode.IMAGE_STORAGE_ERROR,
                    exception
            );
        }
    }

    @Override
    public void delete(
            String storageKey
    ) {
        DeleteObjectRequest request =
                DeleteObjectRequest.builder()
                        .bucket(properties.bucket())
                        .key(storageKey)
                        .build();

        try {
            s3Client.deleteObject(request);

        } catch (SdkException exception) {
            throw new BusinessException(
                    ErrorCode.IMAGE_STORAGE_ERROR,
                    exception
            );
        }
    }

    @Override
    public String getUrl(
            String storageKey
    ) {
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(properties.bucket())
                        .key(storageKey)
                        .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(
                                properties
                                        .presignedUrlDuration()
                        )
                        .getObjectRequest(
                                getObjectRequest
                        )
                        .build();

        try {
            PresignedGetObjectRequest
                    presignedRequest =
                    s3Presigner.presignGetObject(
                            presignRequest
                    );

            return presignedRequest
                    .url()
                    .toExternalForm();

        } catch (SdkException exception) {
            throw new BusinessException(
                    ErrorCode.IMAGE_STORAGE_ERROR,
                    exception
            );
        }
    }

    private String createStorageKey(
            Long postId,
            ImageType imageType
    ) {
        String filename =
                UUID.randomUUID()
                        + "."
                        + imageType
                        .getStorageExtension();

        return "%s/%d/%s".formatted(
                POST_IMAGE_PREFIX,
                postId,
                filename
        );
    }
}