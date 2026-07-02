package com.example.board.domain.comment.controller;

import com.example.board.domain.comment.controller.docs.CommentApiDocs;
import com.example.board.domain.comment.dto.request.CommentCreateRequest;
import com.example.board.domain.comment.dto.request.CommentUpdateRequest;
import com.example.board.domain.comment.dto.response.CommentCreateResponse;
import com.example.board.domain.comment.dto.response.CommentListResponse;
import com.example.board.domain.comment.dto.response.CommentUpdateResponse;
import com.example.board.domain.comment.service.CommentService;
import com.example.board.global.common.response.ApiResponse;
import com.example.board.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApiDocs {

    private final CommentService commentService;

    @Override
    @PostMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentCreateResponse>>
    createComment(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long postId,

            @RequestBody
            CommentCreateRequest request
    ) {
        CommentCreateResponse response =
                commentService.createComment(
                        userDetails.getMemberId(),
                        postId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "COMMENT201",
                                "댓글을 작성했습니다.",
                                response
                        )
                );
    }

    @Override
    @GetMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentListResponse>>
    getComments(
            @PathVariable
            Long postId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        CommentListResponse response =
                commentService.getComments(
                        postId,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "COMMENT200",
                        "댓글 목록을 조회했습니다.",
                        response
                )
        );
    }

    @Override
    @PatchMapping("/api/v1/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentUpdateResponse>>
    updateComment(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long commentId,

            @RequestBody
            CommentUpdateRequest request
    ) {
        CommentUpdateResponse response =
                commentService.updateComment(
                        userDetails.getMemberId(),
                        userDetails.getRole(),
                        commentId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "COMMENT2001",
                        "댓글을 수정했습니다.",
                        response
                )
        );
    }

    @Override
    @DeleteMapping("/api/v1/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>>
    deleteComment(
            @AuthenticationPrincipal
            CustomUserDetails userDetails,

            @PathVariable
            Long commentId
    ) {
        commentService.deleteComment(
                userDetails.getMemberId(),
                userDetails.getRole(),
                commentId
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>success(
                        "COMMENT2002",
                        "댓글을 삭제했습니다.",
                        null
                )
        );
    }
}