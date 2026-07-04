package com.example.board.domain.image.storage.s3;

import com.example.board.domain.image.storage.ImageStorage;
import com.example.board.domain.image.validation.ImageType;
import com.example.board.global.config.properties.S3FileProperties;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
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

    private static final int MAX_DELETE_OBJECTS =
            1000;

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
    public void deleteAll(
            List<String> storageKeys
    ) {
        if (storageKeys == null
                || storageKeys.isEmpty()) {
            return;
        }

        for (
                int start = 0;
                start < storageKeys.size();
                start += MAX_DELETE_OBJECTS
        ) {
            int end = Math.min(
                    start + MAX_DELETE_OBJECTS,
                    storageKeys.size()
            );

            deleteChunk(
                    storageKeys.subList(
                            start,
                            end
                    )
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

    private void deleteChunk(
            List<String> storageKeys
    ) {
        List<ObjectIdentifier> objectIdentifiers =
                storageKeys.stream()
                        .map(storageKey ->
                                ObjectIdentifier
                                        .builder()
                                        .key(storageKey)
                                        .build()
                        )
                        .toList();

        Delete delete =
                Delete.builder()
                        .objects(objectIdentifiers)
                        .quiet(true)
                        .build();

        DeleteObjectsRequest request =
                DeleteObjectsRequest.builder()
                        .bucket(properties.bucket())
                        .delete(delete)
                        .build();

        try {
            DeleteObjectsResponse response =
                    s3Client.deleteObjects(request);

            if (!response.errors().isEmpty()) {
                response.errors().forEach(error ->
                        log.warn(
                                "S3 이미지 삭제 일부 실패. "
                                        + "key={}, code={}, message={}",
                                error.key(),
                                error.code(),
                                error.message()
                        )
                );

                throw new BusinessException(
                        ErrorCode.IMAGE_STORAGE_ERROR
                );
            }

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