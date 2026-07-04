package com.example.board.domain.category.controller.docs;

import com.example.board.domain.category.dto.response.CategoryListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Category",
        description = "공개 카테고리 조회 API"
)
public interface CategoryApiDocs {

    @Operation(
            summary = "활성 카테고리 목록 조회",
            description = """
                    게시글 작성에 사용할 수 있는 ACTIVE 상태의
                    카테고리 목록을 조회합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "활성 카테고리 목록 조회 성공"
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    CategoryListResponse>>
    getCategories();
}