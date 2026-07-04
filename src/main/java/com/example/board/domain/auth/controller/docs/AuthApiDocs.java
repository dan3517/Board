package com.example.board.domain.auth.controller.docs;

import com.example.board.domain.member.dto.request.LoginRequest;
import com.example.board.domain.member.dto.request.ReissueRequest;
import com.example.board.domain.member.dto.request.SignupRequest;
import com.example.board.domain.member.dto.response.SignupResponse;
import com.example.board.domain.member.dto.response.TokenResponse;
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
        name = "Auth",
        description = """
                회원 인증 API입니다.

                회원가입, 로그인, JWT 토큰 재발급,
                로그아웃 기능을 제공합니다.

                로그인 성공 시 Access Token과 Refresh Token을 발급합니다.
                Access Token은 인증이 필요한 API 요청의
                Authorization 헤더에 Bearer 방식으로 전달합니다.
                """
)
public interface AuthApiDocs {

    @Operation(
            summary = "회원가입",
            description = """
                    새로운 회원 계정을 생성합니다.

                    - 이메일은 로그인 ID로 사용됩니다.
                    - 이메일과 닉네임은 중복될 수 없습니다.
                    - 비밀번호는 암호화한 뒤 저장합니다.
                    - 회원가입 후 로그인 API를 호출해 토큰을 발급받아야 합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            요청값 검증 실패

                            이메일 형식 오류, 비밀번호 형식 오류,
                            닉네임 길이 오류 등이 포함됩니다.
                            """,
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
                    responseCode = "409",
                    description = "이메일 또는 닉네임 중복"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
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
                    SignupResponse>>
    signup(
            SignupRequest request
    );

    @Operation(
            summary = "로그인",
            description = """
                    이메일과 비밀번호로 로그인합니다.

                    로그인 성공 시 다음 토큰을 발급합니다.

                    - Access Token: 인증이 필요한 API 요청에 사용
                    - Refresh Token: Access Token 재발급에 사용

                    Swagger UI에서 로그인 응답의 Access Token을 복사한 뒤
                    화면 오른쪽 위 Authorize 버튼에 입력합니다.
                    'Bearer ' 접두사는 직접 입력하지 않습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이메일 또는 비밀번호 요청값 검증 실패",
                    content = @Content(
                            mediaType =
                                    MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ApiValidationErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            로그인 실패

                            이메일이 존재하지 않거나
                            비밀번호가 일치하지 않는 경우입니다.
                            """,
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
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "탈퇴 또는 정지된 회원"
            )
    })
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    TokenResponse>>
    login(
            LoginRequest request
    );

    @Operation(
            summary = "Access Token 재발급",
            description = """
                    유효한 Refresh Token을 사용해
                    새로운 Access Token을 발급합니다.

                    Access Token이 만료된 상황에서 사용하는 API이므로
                    Access Token 인증은 요구하지 않습니다.

                    Refresh Token은 요청 본문으로 전달하며,
                    만료되었거나 서버에 저장된 토큰과 다르면
                    재발급에 실패합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Refresh Token 누락"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            Refresh Token이 유효하지 않음

                            토큰 만료, 서명 오류, 저장 토큰과 불일치,
                            로그아웃으로 삭제된 토큰 등이 포함됩니다.
                            """,
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
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<
                    TokenResponse>>
    reissue(
            ReissueRequest request
    );

    @Operation(
            summary = "로그아웃",
            description = """
                    로그인한 회원을 로그아웃 처리합니다.

                    서버에 저장된 Refresh Token을 제거합니다.
                    로그아웃 후 기존 Refresh Token으로는
                    Access Token을 재발급할 수 없습니다.

                    별도의 Access Token 블랙리스트를 사용하지 않는 경우,
                    이미 발급된 Access Token은 만료될 때까지
                    유효할 수 있습니다.
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
                    description = "로그아웃 성공"
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
    ResponseEntity<
            com.example.board.global.common.response.ApiResponse<Void>>
    logout(
            @Parameter(hidden = true)
            CustomUserDetails userDetails
    );
}