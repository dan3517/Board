package com.example.board.domain.post.controller.docs;

import com.example.board.domain.post.dto.request.PostCreateRequest;
import com.example.board.domain.post.dto.request.PostSearchRequest;
import com.example.board.domain.post.dto.request.PostUpdateRequest;
import com.example.board.domain.post.dto.response.PostCreateResponse;
import com.example.board.domain.post.dto.response.PostDetailResponse;
import com.example.board.domain.post.dto.response.PostListResponse;
import com.example.board.domain.post.dto.response.PostUpdateResponse;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Post",
        description = "게시글 작성, 조회, 수정, 삭제 API"
)
public interface PostApiDocs {

    @Operation(
            summary = "게시글 작성",
            description = """
                    로그인한 회원이 게시글을 작성합니다.
                    ACTIVE 상태인 카테고리만 사용할 수 있습니다.
                    """,
            security = {
                    @SecurityRequirement(
                            name = SwaggerConstants.BEARER_AUTH
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "게시글 작성 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터 검증 실패",
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
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
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
                    description = "회원 또는 카테고리를 찾을 수 없음",
                    content = @Content(
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ApiErrorResponse.class
                            )
                    )
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    PostCreateResponse>>
    createPost(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            PostCreateRequest request
    );

    @Operation(
            summary = "게시글 목록 조회",
            description = """
                    삭제되지 않은 게시글 목록을 조회합니다.

                    검색 조건:
                    - keyword: 제목 또는 내용
                    - author: 작성자 닉네임
                    - categoryId: 카테고리
                    - sort: latest, views, likes

                    page는 0부터 시작합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "게시글 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "페이지 또는 검색 조건 오류",
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
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    PostListResponse>>
    getPosts(
            @ParameterObject
            PostSearchRequest request
    );

    @Operation(
            summary = "게시글 상세 조회",
            description = """
                    게시글 상세 정보를 조회합니다.

                    이 API는 비로그인 사용자도 호출할 수 있습니다.
                    유효한 Access Token을 함께 보내면 likedByMe 값이
                    현재 사용자를 기준으로 계산됩니다.

                    호출할 때마다 조회 수가 증가합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "게시글 상세 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "게시글을 찾을 수 없음",
                    content = @Content(
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
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
                    PostDetailResponse>>
    getPost(
            @Parameter(
                    description = "게시글 ID",
                    required = true,
                    example = "1"
            )
            Long postId,

            @Parameter(hidden = true)
            CustomUserDetails userDetails
    );

    @Operation(
            summary = "게시글 수정",
            description = """
                    게시글 작성자 또는 관리자가 게시글을 수정합니다.
                    삭제된 게시글은 수정할 수 없습니다.
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
                    description = "게시글 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터 검증 실패",
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
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
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
                    responseCode = "403",
                    description = "게시글 수정 권한 없음",
                    content = @Content(
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
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
                    description = "게시글을 찾을 수 없음",
                    content = @Content(
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
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
                    PostUpdateResponse>>
    updatePost(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "게시글 ID",
                    required = true,
                    example = "1"
            )
            Long postId,

            PostUpdateRequest request
    );

    @Operation(
            summary = "게시글 삭제",
            description = """
                    게시글 작성자 또는 관리자가 게시글을 논리 삭제합니다.

                    댓글은 논리 삭제하고, 좋아요는 물리 삭제하며,
                    이미지 파일은 삭제 작업을 통해 저장소에서 제거합니다.
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
                    description = "게시글 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요함",
                    content = @Content(
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
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
                    responseCode = "403",
                    description = "게시글 삭제 권한 없음",
                    content = @Content(
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
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
                    description = "게시글을 찾을 수 없음",
                    content = @Content(
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
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
            com.example.board.global.common.response.ApiResponse<Void>>
    deletePost(
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