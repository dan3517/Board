package com.example.board.domain.image.validation;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public enum ImageType {

    JPEG(
            "image/jpeg",
            "jpg",
            Set.of(
                    "jpg",
                    "jpeg"
            ),
            Set.of(
                    "image/jpeg",
                    "image/jpg"
            )
    ),

    PNG(
            "image/png",
            "png",
            Set.of("png"),
            Set.of("image/png")
    ),

    WEBP(
            "image/webp",
            "webp",
            Set.of("webp"),
            Set.of("image/webp")
    );

    private final String contentType;
    private final String storageExtension;
    private final Set<String> extensions;
    private final Set<String> contentTypes;

    ImageType(
            String contentType,
            String storageExtension,
            Set<String> extensions,
            Set<String> contentTypes
    ) {
        this.contentType = contentType;
        this.storageExtension = storageExtension;
        this.extensions = extensions;
        this.contentTypes = contentTypes;
    }

    public String getContentType() {
        return contentType;
    }

    public String getStorageExtension() {
        return storageExtension;
    }

    public boolean supportsExtension(
            String extension
    ) {
        if (extension == null) {
            return false;
        }

        return extensions.contains(
                extension.toLowerCase(Locale.ROOT)
        );
    }

    public boolean supportsContentType(
            String candidate
    ) {
        if (candidate == null) {
            return false;
        }

        return contentTypes.contains(
                candidate.toLowerCase(Locale.ROOT)
        );
    }

    public static Optional<ImageType> detect(
            byte[] header
    ) {
        if (isJpeg(header)) {
            return Optional.of(JPEG);
        }

        if (isPng(header)) {
            return Optional.of(PNG);
        }

        if (isWebp(header)) {
            return Optional.of(WEBP);
        }

        return Optional.empty();
    }

    private static boolean isJpeg(
            byte[] header
    ) {
        return header.length >= 3
                && unsigned(header[0]) == 0xFF
                && unsigned(header[1]) == 0xD8
                && unsigned(header[2]) == 0xFF;
    }

    private static boolean isPng(
            byte[] header
    ) {
        return header.length >= 8
                && unsigned(header[0]) == 0x89
                && unsigned(header[1]) == 0x50
                && unsigned(header[2]) == 0x4E
                && unsigned(header[3]) == 0x47
                && unsigned(header[4]) == 0x0D
                && unsigned(header[5]) == 0x0A
                && unsigned(header[6]) == 0x1A
                && unsigned(header[7]) == 0x0A;
    }

    private static boolean isWebp(
            byte[] header
    ) {
        return header.length >= 12
                && header[0] == 'R'
                && header[1] == 'I'
                && header[2] == 'F'
                && header[3] == 'F'
                && header[8] == 'W'
                && header[9] == 'E'
                && header[10] == 'B'
                && header[11] == 'P';
    }

    private static int unsigned(byte value) {
        return value & 0xFF;
    }
}