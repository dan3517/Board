package com.example.board.domain.image.storage;

import com.example.board.domain.image.validation.ImageType;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {

    String store(
            Long postId,
            MultipartFile file,
            ImageType imageType
    );

    void delete(String storageKey);

    String getUrl(String storageKey);
}