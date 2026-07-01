package com.example.board.domain.category.controller;

import com.example.board.global.config.MethodSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import static org.assertj.core.api.Assertions.assertThat;

class AdminCategorySecurityDeclarationTest {

    @Test
    @DisplayName("관리자 카테고리 Controller에는 ADMIN 권한이 선언되어 있다")
    void adminCategoryControllerHasAdminRole() {
        // when
        PreAuthorize preAuthorize =
                AdminCategoryController.class
                        .getAnnotation(
                                PreAuthorize.class
                        );

        // then
        assertThat(preAuthorize)
                .isNotNull();

        assertThat(preAuthorize.value())
                .isEqualTo(
                        "hasRole('ADMIN')"
                );
    }

    @Test
    @DisplayName("메서드 보안 기능이 활성화되어 있다")
    void methodSecurityIsEnabled() {
        // when
        boolean enabled =
                MethodSecurityConfig.class
                        .isAnnotationPresent(
                                EnableMethodSecurity.class
                        );

        // then
        assertThat(enabled)
                .isTrue();
    }
}