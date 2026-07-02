package com.example.board.domain.postlike.controller.docs;

import com.example.board.domain.postlike.dto.response.PostLikeResponse;
import com.example.board.global.security.CustomUserDetails;
import com.example.board.global.swagger.SwaggerConstants;
import com.example.board.global.swagger.SwaggerExamples;
import com.example.board.global.swagger.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Post Like",
        description = "게시글 좋아요 등록 및 취소 API"
)
public interface PostLikeApiDocs {

    @Operation(
            summary = "게시글 좋아요 등록",
            description = """
                    게시글에 좋아요를 등록합니다.
                    이미 좋아요한 게시글에 다시 요청해도 중복 저장되지 않습니다.
                    """,
            security = {
                    @SecurityRequirement(
                            name = SwaggerConstants.BEARER_AUTH
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "좋아요 등록 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요함",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            ApiErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    value =
                                            SwaggerExamples
                                                    .UNAUTHORIZED
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "게시글을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            ApiErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    value =
                                            SwaggerExamples
                                                    .POST_NOT_FOUND
                            )
                    )
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    PostLikeResponse>>
    likePost(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "게시글 ID",
                    required = true,
                    example = "1"
            )
            Long postId
    );

    @Operation(
            summary = "게시글 좋아요 취소",
            description = """
                    게시글 좋아요를 취소합니다.
                    좋아요가 없는 상태에서 다시 요청해도 성공합니다.
                    """,
            security = {
                    @SecurityRequirement(
                            name = SwaggerConstants.BEARER_AUTH
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "좋아요 취소 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요함"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "게시글을 찾을 수 없음"
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    PostLikeResponse>>
    unlikePost(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "게시글 ID",
                    required = true,
                    example = "1"
            )
            Long postId
    );
}