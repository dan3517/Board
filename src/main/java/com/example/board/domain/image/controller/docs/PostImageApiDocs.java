package com.example.board.domain.image.controller.docs;

import com.example.board.domain.image.dto.response.PostImageUploadResponse;
import com.example.board.global.security.CustomUserDetails;
import com.example.board.global.swagger.SwaggerConstants;
import com.example.board.global.swagger.SwaggerExamples;
import com.example.board.global.swagger.dto.ApiErrorResponse;
import com.example.board.global.swagger.dto.PostImageUploadSwaggerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(
        name = "Post Image",
        description = "게시글 이미지 업로드 및 삭제 API"
)
public interface PostImageApiDocs {

    @Operation(
            summary = "게시글 이미지 업로드",
            description = """
                    게시글에 이미지를 업로드합니다.

                    - 게시글 작성자 또는 관리자만 호출할 수 있습니다.
                    - JPEG, PNG, WebP 형식을 지원합니다.
                    - 파일 하나당 최대 크기는 10MB입니다.
                    - 게시글 하나당 최대 5개까지 첨부할 수 있습니다.
                    - 같은 files 이름으로 여러 파일을 전송합니다.
                    """,
            security = {
                    @SecurityRequirement(
                            name = SwaggerConstants.BEARER_AUTH
                    )
            }
    )
    @RequestBody(
            required = true,
            description = "업로드할 이미지 파일 목록",
            content = @Content(
                    mediaType =
                            MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(
                            implementation =
                                    PostImageUploadSwaggerRequest.class
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "이미지 업로드 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            이미지 파일 누락, 잘못된 이미지,
                            게시글 이미지 개수 초과
                            """,
                    content = @Content(
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ApiErrorResponse.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "이미지 개수 초과",
                                            value =
                                                    SwaggerExamples
                                                            .IMAGE_COUNT_EXCEEDED
                                    ),
                                    @ExampleObject(
                                            name = "잘못된 요청",
                                            value =
                                                    SwaggerExamples
                                                            .INVALID_INPUT
                                    )
                            }
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
                    description = "게시글 이미지 변경 권한 없음",
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
                                                    .FORBIDDEN
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
            ),
            @ApiResponse(
                    responseCode = "413",
                    description = "이미지 파일 크기 초과",
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
                                                    .IMAGE_TOO_LARGE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "415",
                    description = "지원하지 않는 이미지 형식",
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
                                                    .IMAGE_UNSUPPORTED_TYPE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "이미지 저장소 오류",
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
                                                    .INTERNAL_SERVER_ERROR
                            )
                    )
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    PostImageUploadResponse>>
    uploadImages(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "게시글 ID",
                    required = true,
                    example = "1"
            )
            Long postId,

            @Parameter(hidden = true)
            List<MultipartFile> files
    );

    @Operation(
            summary = "게시글 이미지 삭제",
            description = """
                    게시글 이미지를 삭제합니다.

                    게시글 작성자 또는 관리자만 삭제할 수 있습니다.
                    DB 메타데이터가 먼저 제거되고 저장소 파일은
                    커밋 이후 삭제 작업을 통해 처리됩니다.
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
                    description = "이미지 삭제 요청 성공"
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
                    description = "이미지 삭제 권한 없음",
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
                    description = "게시글 또는 이미지를 찾을 수 없음",
                    content = @Content(
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ApiErrorResponse.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "게시글 없음",
                                            value =
                                                    SwaggerExamples
                                                            .POST_NOT_FOUND
                                    ),
                                    @ExampleObject(
                                            name = "이미지 없음",
                                            value =
                                                    SwaggerExamples
                                                            .IMAGE_NOT_FOUND
                                    )
                            }
                    )
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<Void>>
    deleteImage(
            @Parameter(hidden = true)
            CustomUserDetails userDetails,

            @Parameter(
                    description = "게시글 ID",
                    required = true,
                    example = "1"
            )
            Long postId,

            @Parameter(
                    description = "이미지 ID",
                    required = true,
                    example = "10"
            )
            Long imageId
    );
}