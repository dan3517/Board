package com.example.board.domain.common.docs;

import com.example.board.global.swagger.SwaggerExamples;
import com.example.board.global.swagger.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;

@Tag(
        name = "Health",
        description = """
                서버 상태 확인 API입니다.

                애플리케이션이 실행 중이고 HTTP 요청을 처리할 수 있는지
                배포 환경이나 외부 모니터링 시스템에서 확인할 때 사용합니다.
                """
)
public interface HealthApiDocs {

    @Operation(
            summary = "서버 상태 확인",
            description = """
                    애플리케이션 서버가 정상적으로 실행 중인지 확인합니다.

                    이 API는 인증 없이 호출할 수 있습니다.

                    현재 구현은 서버가 HTTP 요청에 응답할 수 있는지만 확인합니다.
                    데이터베이스, Redis, AWS S3 등의 실제 연결 상태까지
                    검사하는 API는 아닙니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "서버가 정상적으로 요청을 처리함"
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
    com.example.board.global.common.response.ApiResponse<String>
    health();
}