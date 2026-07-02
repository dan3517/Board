package com.example.board.domain.postlike.controller;

import com.example.board.domain.postlike.controller.docs.PostLikeApiDocs;
import com.example.board.domain.postlike.dto.response.PostLikeResponse;
import com.example.board.domain.postlike.service.PostLikeService;
import com.example.board.global.common.response.ApiResponse;
import com.example.board.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/likes")
public class PostLikeController implements PostLikeApiDocs {

    private final PostLikeService postLikeService;

    @Override
    @PutMapping
    public ResponseEntity<ApiResponse<PostLikeResponse>>
    likePost(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long postId
    ) {
        PostLikeResponse response =
                postLikeService.likePost(
                        userDetails.getMemberId(),
                        postId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "LIKE200",
                        "게시글 좋아요를 등록했습니다.",
                        response
                )
        );
    }

    @Override
    @DeleteMapping
    public ResponseEntity<ApiResponse<PostLikeResponse>>
    unlikePost(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long postId
    ) {
        PostLikeResponse response =
                postLikeService.unlikePost(
                        userDetails.getMemberId(),
                        postId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "LIKE2001",
                        "게시글 좋아요를 취소했습니다.",
                        response
                )
        );
    }
}