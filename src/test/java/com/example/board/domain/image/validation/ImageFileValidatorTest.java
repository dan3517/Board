package com.example.board.domain.image.validation;

import com.example.board.global.config.properties.ImageProperties;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ImageFileValidatorTest {

    private ImageFileValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ImageFileValidator(
                new ImageProperties(
                        5,
                        10 * 1024 * 1024
                )
        );
    }

    @Test
    @DisplayName("정상적인 PNG 파일을 검증한다")
    void validatePngSuccess() {
        // given
        MockMultipartFile file =
                new MockMultipartFile(
                        "files",
                        "image.png",
                        "image/png",
                        pngBytes()
                );

        // when
        ValidatedImage result =
                validator.validate(file);

        // then
        assertThat(result.originalFilename())
                .isEqualTo("image.png");

        assertThat(result.imageType())
                .isEqualTo(ImageType.PNG);
    }

    @Test
    @DisplayName("파일 확장자와 실제 이미지 형식이 다르면 실패한다")
    void validateFailsWhenExtensionMismatch() {
        // given
        MockMultipartFile file =
                new MockMultipartFile(
                        "files",
                        "image.jpg",
                        "image/jpeg",
                        pngBytes()
                );

        // when
        Throwable throwable =
                catchThrowable(
                        () -> validator.validate(file)
                );

        // then
        assertThat(throwable)
                .isInstanceOf(
                        BusinessException.class
                );

        BusinessException exception =
                (BusinessException) throwable;

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.IMAGE_INVALID_FILE
                );
    }

    @Test
    @DisplayName("이미지 헤더가 아니면 업로드에 실패한다")
    void validateFailsWhenHeaderInvalid() {
        // given
        MockMultipartFile file =
                new MockMultipartFile(
                        "files",
                        "image.png",
                        "image/png",
                        "not-an-image".getBytes()
                );

        // when
        Throwable throwable =
                catchThrowable(
                        () -> validator.validate(file)
                );

        // then
        assertThat(throwable)
                .isInstanceOf(
                        BusinessException.class
                );

        BusinessException exception =
                (BusinessException) throwable;

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode
                                .IMAGE_UNSUPPORTED_TYPE
                );
    }

    @Test
    @DisplayName("빈 파일이면 업로드에 실패한다")
    void validateFailsWhenFileEmpty() {
        // given
        MockMultipartFile file =
                new MockMultipartFile(
                        "files",
                        "image.png",
                        "image/png",
                        new byte[0]
                );

        // when
        Throwable throwable =
                catchThrowable(
                        () -> validator.validate(file)
                );

        // then
        assertThat(throwable)
                .isInstanceOf(
                        BusinessException.class
                );

        BusinessException exception =
                (BusinessException) throwable;

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.IMAGE_FILE_REQUIRED
                );
    }

    private byte[] pngBytes() {
        return new byte[]{
                (byte) 0x89,
                0x50,
                0x4E,
                0x47,
                0x0D,
                0x0A,
                0x1A,
                0x0A,
                0x00,
                0x00,
                0x00,
                0x00
        };
    }
}