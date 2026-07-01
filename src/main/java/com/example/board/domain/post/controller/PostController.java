package com.example.board.domain.post.controller;

import com.example.board.domain.post.dto.request.PostCreateRequest;
import com.example.board.domain.post.dto.response.PostCreateResponse;
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
public class PostController {

    private final PostService postService;

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
}