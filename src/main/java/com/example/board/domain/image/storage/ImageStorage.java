package com.example.board.domain.image.storage;

import com.example.board.domain.image.validation.ImageType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorage {

    String store(
            Long postId,
            MultipartFile file,
            ImageType imageType
    );

    void delete(String storageKey);

    default void deleteAll(
            List<String> storageKeys
    ) {
        if (storageKeys == null
                || storageKeys.isEmpty()) {
            return;
        }

        for (String storageKey : storageKeys) {
            delete(storageKey);
        }
    }

    String getUrl(String storageKey);
}