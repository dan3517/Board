package com.example.board.domain.image.validation;

public record ValidatedImage(
        String originalFilename,
        ImageType imageType
) {
}