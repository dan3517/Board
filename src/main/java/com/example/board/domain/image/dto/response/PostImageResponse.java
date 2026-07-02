package com.example.board.domain.image.dto.response;

import com.example.board.domain.image.entity.PostImage;

public record PostImageResponse(
        Long imageId,
        String imageUrl,
        String originalFilename,
        String contentType,
        long fileSize,
        int sortOrder
) {

    public static PostImageResponse from(
            PostImage image,
            String imageUrl
    ) {
        return new PostImageResponse(
                image.getId(),
                imageUrl,
                image.getOriginalFilename(),
                image.getContentType(),
                image.getFileSize(),
                image.getSortOrder()
        );
    }
}