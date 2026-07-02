package com.example.board.domain.image.storage.s3;

import com.example.board.domain.image.validation.ImageType;
import com.example.board.global.config.properties.S3FileProperties;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class S3ImageStorageTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private PresignedGetObjectRequest
            presignedGetObjectRequest;

    private S3ImageStorage s3ImageStorage;

    @BeforeEach
    void setUp() {
        S3FileProperties properties =
                new S3FileProperties(
                        "test-board-bucket",
                        "ap-northeast-2",
                        10
                );

        s3ImageStorage =
                new S3ImageStorage(
                        s3Client,
                        s3Presigner,
                        properties
                );
    }

    @Test
    @DisplayName("S3에 이미지를 업로드하고 storageKey를 반환한다")
    void storeSuccess() {
        // given
        Long postId = 10L;

        MockMultipartFile file =
                new MockMultipartFile(
                        "files",
                        "image.png",
                        "image/png",
                        pngBytes()
                );

        given(
                s3Client.putObject(
                        any(PutObjectRequest.class),
                        any(RequestBody.class)
                )
        ).willReturn(
                PutObjectResponse.builder()
                        .eTag("test-etag")
                        .build()
        );

        // when
        String storageKey =
                s3ImageStorage.store(
                        postId,
                        file,
                        ImageType.PNG
                );

        // then
        assertThat(storageKey)
                .startsWith("posts/10/");

        assertThat(storageKey)
                .endsWith(".png");

        ArgumentCaptor<PutObjectRequest>
                requestCaptor =
                ArgumentCaptor.forClass(
                        PutObjectRequest.class
                );

        then(s3Client)
                .should()
                .putObject(
                        requestCaptor.capture(),
                        any(RequestBody.class)
                );

        PutObjectRequest request =
                requestCaptor.getValue();

        assertThat(request.bucket())
                .isEqualTo("test-board-bucket");

        assertThat(request.key())
                .isEqualTo(storageKey);

        assertThat(request.contentType())
                .isEqualTo("image/png");

        assertThat(request.contentLength())
                .isEqualTo((long) pngBytes().length);
    }

    @Test
    @DisplayName("S3에서 객체를 삭제한다")
    void deleteSuccess() {
        // given
        String storageKey =
                "posts/10/test-image.png";

        given(
                s3Client.deleteObject(
                        any(DeleteObjectRequest.class)
                )
        ).willReturn(
                DeleteObjectResponse.builder()
                        .build()
        );

        // when
        s3ImageStorage.delete(storageKey);

        // then
        ArgumentCaptor<DeleteObjectRequest>
                requestCaptor =
                ArgumentCaptor.forClass(
                        DeleteObjectRequest.class
                );

        then(s3Client)
                .should()
                .deleteObject(
                        requestCaptor.capture()
                );

        DeleteObjectRequest request =
                requestCaptor.getValue();

        assertThat(request.bucket())
                .isEqualTo("test-board-bucket");

        assertThat(request.key())
                .isEqualTo(storageKey);
    }

    @Test
    @DisplayName("S3 객체에 접근할 presigned URL을 생성한다")
    void getUrlSuccess() throws Exception {
        // given
        String storageKey =
                "posts/10/test-image.png";

        String expectedUrl =
                "https://test-board-bucket"
                        + ".s3.ap-northeast-2.amazonaws.com/"
                        + storageKey
                        + "?X-Amz-Signature=test";

        given(
                s3Presigner.presignGetObject(
                        any(
                                GetObjectPresignRequest
                                        .class
                        )
                )
        ).willReturn(
                presignedGetObjectRequest
        );

        given(
                presignedGetObjectRequest.url()
        ).willReturn(
                URI.create(expectedUrl).toURL()
        );

        // when
        String result =
                s3ImageStorage.getUrl(
                        storageKey
                );

        // then
        assertThat(result)
                .isEqualTo(expectedUrl);

        ArgumentCaptor<GetObjectPresignRequest>
                requestCaptor =
                ArgumentCaptor.forClass(
                        GetObjectPresignRequest.class
                );

        then(s3Presigner)
                .should()
                .presignGetObject(
                        requestCaptor.capture()
                );

        GetObjectPresignRequest request =
                requestCaptor.getValue();

        assertThat(request.signatureDuration())
                .hasMinutes(10);

        assertThat(
                request.getObjectRequest()
                        .bucket()
        ).isEqualTo(
                "test-board-bucket"
        );

        assertThat(
                request.getObjectRequest()
                        .key()
        ).isEqualTo(storageKey);
    }

    @Test
    @DisplayName("S3 업로드에 실패하면 이미지 저장 예외를 반환한다")
    void storeFailsWhenS3RequestFails() {
        // given
        MockMultipartFile file =
                new MockMultipartFile(
                        "files",
                        "image.png",
                        "image/png",
                        pngBytes()
                );

        S3Exception s3Exception =
                (S3Exception) S3Exception.builder()
                        .message("Access Denied")
                        .statusCode(403)
                        .build();

        given(
                s3Client.putObject(
                        any(PutObjectRequest.class),
                        any(RequestBody.class)
                )
        ).willThrow(s3Exception);

        // when
        Throwable throwable =
                catchThrowable(
                        () -> s3ImageStorage.store(
                                10L,
                                file,
                                ImageType.PNG
                        )
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
                        ErrorCode.IMAGE_STORAGE_ERROR
                );

        assertThat(exception.getCause())
                .isSameAs(s3Exception);
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