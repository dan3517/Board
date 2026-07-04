package com.example.board.domain.member.controller.docs;

import com.example.board.domain.member.dto.response.MyProfileResponse;
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
import org.springframework.http.MediaType;

@Tag(
        name = "Member",
        description = """
                로그인한 회원의 정보 조회 및 계정 관리 API입니다.

                현재는 로그인한 회원 자신의 프로필을 조회하는
                API를 제공합니다.
                """
)
public interface MemberApiDocs {

    @Operation(
            summary = "내 프로필 조회",
            description = """
                    현재 로그인한 회원의 프로필을 조회합니다.

                    JWT Access Token을 통해 인증된 회원 정보를 기준으로
                    회원 ID, 이메일, 닉네임, 역할 등의 정보를 반환합니다.

                    비밀번호와 Refresh Token 같은 민감한 정보는
                    응답에 포함하지 않습니다.
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
                    description = "내 프로필 조회 성공"
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
                                            SwaggerExamples.UNAUTHORIZED
                            )
                    )
            )
    })
    com.example.board.global.common.response.ApiResponse<
            MyProfileResponse>
    getMyProfile(
            @Parameter(hidden = true)
            CustomUserDetails userDetails
    );
}