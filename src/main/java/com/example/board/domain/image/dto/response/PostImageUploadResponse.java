package com.example.board.domain.image.dto.response;

import java.util.List;

public record PostImageUploadResponse(
        List<PostImageResponse> images
) {
}