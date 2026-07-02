package com.example.board.domain.category.controller.docs;

import com.example.board.domain.category.dto.request.CategoryCreateRequest;
import com.example.board.domain.category.dto.request.CategoryStatusUpdateRequest;
import com.example.board.domain.category.dto.request.CategoryUpdateRequest;
import com.example.board.domain.category.dto.response.CategoryCreateResponse;
import com.example.board.domain.category.dto.response.CategoryResponse;
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
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Admin Category",
        description = "관리자 카테고리 관리 API"
)
public interface AdminCategoryApiDocs {

    @Operation(
            summary = "카테고리 생성",
            description = "관리자가 새로운 카테고리를 생성합니다.",
            security = {
                    @SecurityRequirement(
                            name = SwaggerConstants.BEARER_AUTH
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "카테고리 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이름 검증 실패",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            ApiValidationErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요함"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한이 필요함",
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
                    responseCode = "409",
                    description = "카테고리 이름 중복"
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    CategoryCreateResponse>>
    createCategory(
            CategoryCreateRequest request
    );

    @Operation(
            summary = "카테고리 이름 수정",
            description = "관리자가 카테고리 이름을 수정합니다.",
            security = {
                    @SecurityRequirement(
                            name = SwaggerConstants.BEARER_AUTH
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "카테고리 이름 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한이 필요함"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "카테고리를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            ApiErrorResponse.class
                            ),
                            examples = @ExampleObject(
                                    value =
                                            SwaggerExamples
                                                    .CATEGORY_NOT_FOUND
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "카테고리 이름 중복"
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    CategoryResponse>>
    updateCategory(
            @Parameter(
                    description = "카테고리 ID",
                    required = true,
                    example = "1"
            )
            Long categoryId,

            CategoryUpdateRequest request
    );

    @Operation(
            summary = "카테고리 상태 수정",
            description = """
                    관리자가 카테고리를 활성화하거나 비활성화합니다.
                    비활성화된 카테고리는 새 게시글 작성에 사용할 수 없습니다.
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
                    description = "카테고리 상태 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한이 필요함"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "카테고리를 찾을 수 없음"
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    CategoryResponse>>
    updateCategoryStatus(
            @Parameter(
                    description = "카테고리 ID",
                    required = true,
                    example = "1"
            )
            Long categoryId,

            CategoryStatusUpdateRequest request
    );
}