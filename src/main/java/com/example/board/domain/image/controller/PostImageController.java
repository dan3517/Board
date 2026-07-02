package com.example.board.domain.image.controller;

import com.example.board.domain.image.dto.response.PostImageUploadResponse;
import com.example.board.domain.image.service.PostImageService;
import com.example.board.global.common.response.ApiResponse;
import com.example.board.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(
        "/api/v1/posts/{postId}/images"
)
public class PostImageController {

    private final PostImageService postImageService;

    @PostMapping(
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<
            ApiResponse<PostImageUploadResponse>>
    uploadImages(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long postId,

            @RequestParam(
                    name = "files",
                    required = false
            )
            List<MultipartFile> files
    ) {
        PostImageUploadResponse response =
                postImageService.uploadImages(
                        userDetails.getMemberId(),
                        userDetails.getRole(),
                        postId,
                        files
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "IMAGE201",
                                "이미지를 업로드했습니다.",
                                response
                        )
                );
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse<Void>>
    deleteImage(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long postId,

            @PathVariable
            Long imageId
    ) {
        postImageService.deleteImage(
                userDetails.getMemberId(),
                userDetails.getRole(),
                postId,
                imageId
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>success(
                        "IMAGE2001",
                        "이미지를 삭제했습니다.",
                        null
                )
        );
    }
}