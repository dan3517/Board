package com.example.board.domain.image.validation;

import com.example.board.global.config.properties.ImageProperties;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ImageFileValidator {

    private static final int HEADER_LENGTH = 12;
    private static final int MAX_ORIGINAL_FILENAME_LENGTH = 255;

    private final ImageProperties imageProperties;

    public ValidatedImage validate(
            MultipartFile file
    ) {
        validateNotEmpty(file);
        validateSize(file);

        String originalFilename =
                validateAndNormalizeFilename(
                        file.getOriginalFilename()
                );

        String extension =
                extractExtension(originalFilename);

        ImageType detectedType =
                detectImageType(file);

        if (!detectedType.supportsExtension(extension)) {
            throw new BusinessException(
                    ErrorCode.IMAGE_INVALID_FILE
            );
        }

        if (!detectedType.supportsContentType(
                file.getContentType()
        )) {
            throw new BusinessException(
                    ErrorCode.IMAGE_INVALID_FILE
            );
        }

        return new ValidatedImage(
                originalFilename,
                detectedType
        );
    }

    private void validateNotEmpty(
            MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.IMAGE_FILE_REQUIRED
            );
        }
    }

    private void validateSize(
            MultipartFile file
    ) {
        if (file.getSize()
                > imageProperties.maxSizeBytes()) {

            throw new BusinessException(
                    ErrorCode.IMAGE_TOO_LARGE
            );
        }
    }

    private String validateAndNormalizeFilename(
            String originalFilename
    ) {
        if (!StringUtils.hasText(
                originalFilename
        )) {
            throw new BusinessException(
                    ErrorCode.IMAGE_INVALID_FILE
            );
        }

        String normalized =
                originalFilename.strip();

        if (normalized.length()
                > MAX_ORIGINAL_FILENAME_LENGTH) {

            throw new BusinessException(
                    ErrorCode.IMAGE_INVALID_FILE
            );
        }

        if (normalized.contains("..")
                || normalized.contains("/")
                || normalized.contains("\\")) {

            throw new BusinessException(
                    ErrorCode.IMAGE_INVALID_FILE
            );
        }

        return normalized;
    }

    private String extractExtension(
            String filename
    ) {
        int lastDotIndex =
                filename.lastIndexOf('.');

        if (lastDotIndex <= 0
                || lastDotIndex
                == filename.length() - 1) {

            throw new BusinessException(
                    ErrorCode.IMAGE_UNSUPPORTED_TYPE
            );
        }

        return filename
                .substring(lastDotIndex + 1)
                .toLowerCase(Locale.ROOT);
    }

    private ImageType detectImageType(
            MultipartFile file
    ) {
        try (
                InputStream inputStream =
                        file.getInputStream()
        ) {
            byte[] header =
                    inputStream.readNBytes(
                            HEADER_LENGTH
                    );

            return ImageType.detect(header)
                    .orElseThrow(
                            () -> new BusinessException(
                                    ErrorCode
                                            .IMAGE_UNSUPPORTED_TYPE
                            )
                    );

        } catch (IOException exception) {
            throw new BusinessException(
                    ErrorCode.IMAGE_INVALID_FILE,
                    exception
            );
        }
    }
}