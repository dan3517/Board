package com.example.board.global.config;

import com.example.board.global.swagger.SwaggerConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@OpenAPIDefinition(
        info = @Info(
                title = "Board API",
                version = "v1",
                description = """
                        Spring Boot 4와 Java 21로 구현한 게시판 REST API입니다.

                        인증 API에서 Access Token을 발급받은 뒤,
                        Swagger UI의 Authorize 버튼에 Access Token을 입력하면
                        인증이 필요한 API를 호출할 수 있습니다.
                        """,
                contact = @Contact(
                        name = "Board Backend Team"
                ),
                license = @License(
                        name = "Private Project"
                )
        ),
        servers = {
                @Server(
                        url = "/",
                        description = "현재 실행 중인 서버"
                )
        }
)
@SecurityScheme(
        name = SwaggerConstants.BEARER_AUTH,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = """
                로그인 API에서 발급받은 Access Token을 입력합니다.
                'Bearer ' 접두사는 직접 입력하지 않고 토큰 문자열만 입력합니다.
                """
)
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi boardOpenApi() {
        return GroupedOpenApi.builder()
                .group("board-api")
                .displayName("Board API")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}