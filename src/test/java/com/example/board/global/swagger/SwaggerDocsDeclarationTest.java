package com.example.board.global.swagger;

import com.example.board.domain.image.controller.docs.PostImageApiDocs;
import com.example.board.domain.post.controller.docs.PostApiDocs;
import com.example.board.global.config.SwaggerConfig;
import com.example.board.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerDocsDeclarationTest {

    @Test
    @DisplayName("Swagger에는 JWT Bearer 인증 방식이 선언되어 있다")
    void bearerSecuritySchemeIsDeclared() {
        // when
        SecurityScheme securityScheme =
                SwaggerConfig.class.getAnnotation(
                        SecurityScheme.class
                );

        // then
        assertThat(securityScheme)
                .isNotNull();

        assertThat(securityScheme.name())
                .isEqualTo(
                        SwaggerConstants.BEARER_AUTH
                );

        assertThat(securityScheme.type())
                .isEqualTo(
                        SecuritySchemeType.HTTP
                );

        assertThat(securityScheme.scheme())
                .isEqualTo("bearer");

        assertThat(securityScheme.bearerFormat())
                .isEqualTo("JWT");
    }

    @Test
    @DisplayName("이미지 업로드는 multipart 요청으로 문서화되어 있다")
    void imageUploadIsDocumentedAsMultipart()
            throws NoSuchMethodException {

        // given
        Method method =
                PostImageApiDocs.class.getMethod(
                        "uploadImages",
                        CustomUserDetails.class,
                        Long.class,
                        List.class
                );

        // when
        RequestBody requestBody =
                method.getAnnotation(
                        RequestBody.class
                );

        // then
        assertThat(requestBody)
                .isNotNull();

        assertThat(requestBody.required())
                .isTrue();

        assertThat(requestBody.content())
                .hasSize(1);

        assertThat(
                requestBody.content()[0]
                        .mediaType()
        ).isEqualTo(
                "multipart/form-data"
        );
    }

    @Test
    @DisplayName("이미지 업로드 API에는 Bearer 인증이 선언되어 있다")
    void imageUploadRequiresBearerAuthentication()
            throws NoSuchMethodException {

        // given
        Method method =
                PostImageApiDocs.class.getMethod(
                        "uploadImages",
                        CustomUserDetails.class,
                        Long.class,
                        List.class
                );

        // when
        Operation operation =
                method.getAnnotation(
                        Operation.class
                );

        // then
        assertThat(operation)
                .isNotNull();

        assertThat(operation.security())
                .hasSize(1);

        assertThat(
                operation.security()[0]
                        .name()
        ).isEqualTo(
                SwaggerConstants.BEARER_AUTH
        );
    }

    @Test
    @DisplayName("게시글 상세 조회는 공개 API로 문서화되어 있다")
    void postDetailDoesNotRequireAuthentication()
            throws NoSuchMethodException {

        // given
        Method method =
                PostApiDocs.class.getMethod(
                        "getPost",
                        Long.class,
                        CustomUserDetails.class
                );

        // when
        Operation operation =
                method.getAnnotation(
                        Operation.class
                );

        // then
        assertThat(operation)
                .isNotNull();

        assertThat(operation.security())
                .isEmpty();
    }
}