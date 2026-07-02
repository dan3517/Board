package com.example.board.domain.comment.controller.docs;

import com.example.board.domain.comment.dto.request.CommentCreateRequest;
import com.example.board.domain.comment.dto.request.CommentUpdateRequest;
import com.example.board.domain.comment.dto.response.CommentCreateResponse;
import com.example.board.domain.comment.dto.response.CommentListResponse;
import com.example.board.domain.comment.dto.response.CommentUpdateResponse;
import com.example.board.global.security.CustomUserDetails;
import com.example.board.global.swagger.SwaggerConstants;
import com.example.board.global.swagger.SwaggerExamples;
import com.example.board.global.swagger.dto.ApiErrorResponse;
import com.example.board.global.swagger.dto.ApiValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Comment",
        description = "댓글 작성, 조회, 수정, 삭제 API"
)
public interface CommentApiDocs {

    @Operation(
            summary = "댓글 작성",
            description = "로그인한 회원이 게시글에 댓글을 작성합니다.",
            security = {
                    @SecurityRequirement(
                            name = SwaggerConstants.BEARER_AUTH
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "댓글 작성 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "댓글 내용 검증 실패",
                    content = @Content(
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ApiValidationErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    value =
                                            SwaggerExamples
                                                    .VALIDATION_ERROR
                            )
                    )
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
                    CommentCreateResponse>>
    createComment(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "게시글 ID",
                    required = true,
                    example = "1"
            )
            Long postId,

            CommentCreateRequest request
    );

    @Operation(
            summary = "댓글 목록 조회",
            description = """
                    게시글의 삭제되지 않은 댓글 목록을 조회합니다.
                    댓글은 작성 시각 오름차순으로 정렬됩니다.
                    page는 0부터 시작합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "댓글 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "페이지 조건 오류"
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
                    CommentListResponse>>
    getComments(
            @Parameter(
                    description = "게시글 ID",
                    required = true,
                    example = "1"
            )
            Long postId,

            @Parameter(
                    description = "페이지 번호, 0부터 시작",
                    example = "0"
            )
            int page,

            @Parameter(
                    description = "페이지 크기, 최대 100",
                    example = "20"
            )
            int size
    );

    @Operation(
            summary = "댓글 수정",
            description = "댓글 작성자 또는 관리자가 댓글을 수정합니다.",
            security = {
                    @SecurityRequirement(
                            name = SwaggerConstants.BEARER_AUTH
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "댓글 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요함"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "댓글 수정 권한 없음",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            ApiErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    value =
                                            SwaggerExamples.FORBIDDEN
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "댓글을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            ApiErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    value =
                                            SwaggerExamples
                                                    .COMMENT_NOT_FOUND
                            )
                    )
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    CommentUpdateResponse>>
    updateComment(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "댓글 ID",
                    required = true,
                    example = "1"
            )
            Long commentId,

            CommentUpdateRequest request
    );

    @Operation(
            summary = "댓글 삭제",
            description = "댓글 작성자 또는 관리자가 댓글을 논리 삭제합니다.",
            security = {
                    @SecurityRequirement(
                            name = SwaggerConstants.BEARER_AUTH
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "댓글 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요함"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "댓글 삭제 권한 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "댓글을 찾을 수 없음"
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<Void>>
    deleteComment(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "댓글 ID",
                    required = true,
                    example = "1"
            )
            Long commentId
    );
}