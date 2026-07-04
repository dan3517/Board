package com.example.board.domain.image.storage.local;

import com.example.board.domain.image.storage.ImageStorage;
import com.example.board.domain.image.validation.ImageType;
import com.example.board.global.config.properties.LocalFileProperties;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "app.file",
        name = "storage",
        havingValue = "local",
        matchIfMissing = true
)
public class LocalImageStorage
        implements ImageStorage {

    private final LocalFileProperties properties;

    private Path rootPath;

    @PostConstruct
    void initialize() {
        rootPath = Path.of(
                        properties.rootPath()
                )
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(rootPath);

        } catch (IOException exception) {
            throw new BusinessException(
                    ErrorCode.IMAGE_STORAGE_ERROR,
                    exception
            );
        }
    }

    @Override
    public String store(
            Long postId,
            MultipartFile file,
            ImageType imageType
    ) {
        String filename =
                UUID.randomUUID()
                        + "."
                        + imageType
                        .getStorageExtension();

        String storageKey =
                "posts/"
                        + postId
                        + "/"
                        + filename;

        Path targetPath =
                resolveSafePath(storageKey);

        try {
            Files.createDirectories(
                    targetPath.getParent()
            );

            try (
                    InputStream inputStream =
                            file.getInputStream()
            ) {
                Files.copy(
                        inputStream,
                        targetPath,
                        StandardCopyOption
                                .REPLACE_EXISTING
                );
            }

            return storageKey;

        } catch (IOException exception) {
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
        Path targetPath =
                resolveSafePath(storageKey);

        try {
            Files.deleteIfExists(targetPath);

            deleteParentDirectoryIfEmpty(
                    targetPath.getParent()
            );

        } catch (IOException exception) {
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
        String baseUrl =
                removeTrailingSlash(
                        properties.publicBaseUrl()
                );

        return baseUrl
                + "/"
                + storageKey;
    }

    private Path resolveSafePath(
            String storageKey
    ) {
        Path resolvedPath =
                rootPath.resolve(storageKey)
                        .normalize();

        if (!resolvedPath.startsWith(rootPath)) {
            throw new BusinessException(
                    ErrorCode.IMAGE_INVALID_FILE
            );
        }

        return resolvedPath;
    }

    private void deleteParentDirectoryIfEmpty(
            Path directory
    ) throws IOException {
        if (directory == null
                || directory.equals(rootPath)
                || !Files.isDirectory(directory)) {
            return;
        }

        try (var entries = Files.list(directory)) {
            if (entries.findAny().isEmpty()) {
                Files.deleteIfExists(directory);
            }
        }
    }

    private String removeTrailingSlash(
            String value
    ) {
        String result = value.strip();

        while (result.endsWith("/")) {
            result = result.substring(
                    0,
                    result.length() - 1
            );
        }

        return result;
    }
}