package com.example.board.domain.image.service;

import com.example.board.domain.image.cleanup.service.ImageDeleteTaskCoordinator;
import com.example.board.domain.image.dto.response.PostImageResponse;
import com.example.board.domain.image.dto.response.PostImageUploadResponse;
import com.example.board.domain.image.entity.PostImage;
import com.example.board.domain.image.repository.PostImageRepository;
import com.example.board.domain.image.storage.ImageStorage;
import com.example.board.domain.image.validation.ImageFileValidator;
import com.example.board.domain.image.validation.ValidatedImage;
import com.example.board.domain.member.entity.MemberRole;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.entity.PostStatus;
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.global.config.properties.ImageProperties;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostImageService {

    private final PostImageRepository
            postImageRepository;

    private final PostRepository
            postRepository;

    private final ImageStorage
            imageStorage;

    private final ImageFileValidator
            imageFileValidator;

    private final ImageProperties
            imageProperties;

    private final ImageDeleteTaskCoordinator
            imageDeleteTaskCoordinator;

    @Transactional
    public PostImageUploadResponse uploadImages(
            Long memberId,
            MemberRole memberRole,
            Long postId,
            List<MultipartFile> files
    ) {
        validateFilesExist(files);

        Post post =
                findPublishedPostForUpdate(postId);

        validateModificationAuthority(
                post,
                memberId,
                memberRole
        );

        validateImageCount(
                postId,
                files.size()
        );

        List<ValidatedImage> validatedImages =
                files.stream()
                        .map(
                                imageFileValidator::validate
                        )
                        .toList();

        int nextSortOrder =
                getNextSortOrder(postId);

        List<String> storedKeys =
                new ArrayList<>();

        try {
            List<PostImage> images =
                    new ArrayList<>();

            for (
                    int index = 0;
                    index < files.size();
                    index++
            ) {
                MultipartFile file =
                        files.get(index);

                ValidatedImage validatedImage =
                        validatedImages.get(index);

                String storageKey =
                        imageStorage.store(
                                postId,
                                file,
                                validatedImage.imageType()
                        );

                storedKeys.add(storageKey);

                PostImage image =
                        PostImage.create(
                                post,
                                validatedImage
                                        .originalFilename(),
                                storageKey,
                                validatedImage
                                        .imageType()
                                        .getContentType(),
                                file.getSize(),
                                nextSortOrder + index
                        );

                images.add(image);
            }

            List<PostImage> savedImages =
                    postImageRepository
                            .saveAllAndFlush(images);

            return new PostImageUploadResponse(
                    toResponses(savedImages)
            );

        } catch (RuntimeException exception) {
            cleanupStoredFiles(storedKeys);
            throw exception;
        }
    }

    @Transactional
    public void deleteImage(
            Long memberId,
            MemberRole memberRole,
            Long postId,
            Long imageId
    ) {
        Post post =
                findPublishedPostForUpdate(postId);

        validateModificationAuthority(
                post,
                memberId,
                memberRole
        );

        PostImage image =
                postImageRepository
                        .findByIdAndPostId(
                                imageId,
                                postId
                        )
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                ErrorCode
                                                        .IMAGE_NOT_FOUND
                                        )
                        );

        imageDeleteTaskCoordinator.enqueue(
                List.of(image)
        );
    }

    @Transactional(
            propagation = Propagation.MANDATORY
    )
    public void enqueueAllImagesForDeletion(
            Long postId
    ) {
        List<PostImage> images =
                postImageRepository
                        .findAllByPostIdOrderBySortOrderAsc(
                                postId
                        );

        imageDeleteTaskCoordinator.enqueue(images);
    }

    public List<PostImageResponse>
    getImagesForPost(
            Long postId
    ) {
        return toResponses(
                postImageRepository
                        .findAllByPostIdOrderBySortOrderAsc(
                                postId
                        )
        );
    }

    private void validateFilesExist(
            List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.IMAGE_FILE_REQUIRED
            );
        }
    }

    private void validateImageCount(
            Long postId,
            int uploadCount
    ) {
        long currentCount =
                postImageRepository
                        .countByPostId(postId);

        if (currentCount + uploadCount
                > imageProperties.maxCount()) {

            throw new BusinessException(
                    ErrorCode.IMAGE_COUNT_EXCEEDED
            );
        }
    }

    private int getNextSortOrder(
            Long postId
    ) {
        Integer maxSortOrder =
                postImageRepository
                        .findMaxSortOrderByPostId(
                                postId
                        );

        return maxSortOrder == null
                ? 0
                : maxSortOrder + 1;
    }

    private Post findPublishedPostForUpdate(
            Long postId
    ) {
        return postRepository
                .findByIdAndStatusForUpdate(
                        postId,
                        PostStatus.PUBLISHED
                )
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.POST_NOT_FOUND
                        )
                );
    }

    private void validateModificationAuthority(
            Post post,
            Long memberId,
            MemberRole memberRole
    ) {
        boolean isAdmin =
                memberRole == MemberRole.ADMIN;

        boolean isAuthor =
                post.isAuthor(memberId);

        if (!isAdmin && !isAuthor) {
            throw new BusinessException(
                    ErrorCode.POST_ACCESS_DENIED
            );
        }
    }

    private List<PostImageResponse> toResponses(
            List<PostImage> images
    ) {
        return images.stream()
                .map(image ->
                        PostImageResponse.from(
                                image,
                                imageStorage.getUrl(
                                        image.getStorageKey()
                                )
                        )
                )
                .toList();
    }

    private void cleanupStoredFiles(
            List<String> storedKeys
    ) {
        for (String storageKey : storedKeys) {
            try {
                imageStorage.delete(storageKey);

            } catch (RuntimeException cleanupException) {
                log.error(
                        "업로드 실패 후 이미지 정리 실패. "
                                + "storageKey={}",
                        storageKey,
                        cleanupException
                );
            }
        }
    }
}