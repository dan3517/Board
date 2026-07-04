package com.example.board.domain.post.controller;

import com.example.board.domain.post.dto.request.PostCreateRequest;
import com.example.board.domain.post.dto.request.PostSearchRequest;
import com.example.board.domain.post.dto.request.PostUpdateRequest;
import com.example.board.domain.post.dto.response.PostCreateResponse;
import com.example.board.domain.post.dto.response.PostDetailResponse;
import com.example.board.domain.post.dto.response.PostListResponse;
import com.example.board.domain.post.dto.response.PostUpdateResponse;
import com.example.board.domain.post.service.PostService;
import com.example.board.global.common.response.ApiResponse;
import com.example.board.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController implements com.example.board.domain.post.controller.docs.PostApiDocs {

    private final PostService postService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<PostCreateResponse>>
    createPost(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @Valid
            @RequestBody
            PostCreateRequest request
    ) {
        PostCreateResponse response =
                postService.createPost(
                        userDetails.getMemberId(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "POST201",
                                "게시글을 작성했습니다.",
                                response
                        )
                );
    }

    @Override
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>>
    getPost(
            @PathVariable
            Long postId,

            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {
        Long currentMemberId =
                userDetails == null
                        ? null
                        : userDetails.getMemberId();

        PostDetailResponse response =
                postService.getPost(
                        postId,
                        currentMemberId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "POST200",
                        "게시글을 조회했습니다.",
                        response
                )
        );
    }

    @Override
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostUpdateResponse>>
    updatePost(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long postId,

            @Valid
            @RequestBody
            PostUpdateRequest request
    ) {
        PostUpdateResponse response =
                postService.updatePost(
                        userDetails.getMemberId(),
                        userDetails.getRole(),
                        postId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "POST2001",
                        "게시글을 수정했습니다.",
                        response
                )
        );
    }

    @Override
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>>
    deletePost(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long postId
    ) {
        postService.deletePost(
                userDetails.getMemberId(),
                userDetails.getRole(),
                postId
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>success(
                        "POST2002",
                        "게시글을 삭제했습니다.",
                        null
                )
        );
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PostListResponse>>
    getPosts(
            @Valid
            @ModelAttribute
            PostSearchRequest request
    ) {
        PostListResponse response =
                postService.getPosts(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "POST2003",
                        "게시글 목록을 조회했습니다.",
                        response
                )
        );
    }
}