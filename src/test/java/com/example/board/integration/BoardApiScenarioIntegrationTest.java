package com.example.board.integration;

import com.example.board.domain.category.entity.Category;
import com.example.board.domain.category.repository.CategoryRepository;
import com.example.board.domain.image.entity.PostImage;
import com.example.board.domain.image.repository.PostImageRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@Testcontainers
@ActiveProfiles("integration")
@AutoConfigureTestRestTemplate
@SpringBootTest(
        webEnvironment =
                SpringBootTest.WebEnvironment.RANDOM_PORT
)
class BoardApiScenarioIntegrationTest {

    private static final String TEST_JWT_SECRET =
            "aW50ZWdyYXRpb24tdGVzdC1qd3Qtc2VjcmV0"
                    + "LWtleS0xMjM0NTY=";

    private static final Path TEST_UPLOAD_ROOT =
            createTestUploadRoot();

    private static final byte[] TEST_PNG_BYTES =
            new byte[]{
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

    @Container
    @ServiceConnection
    static final MySQLContainer MYSQL =
            new MySQLContainer(
                    "mysql:8.0.36"
            )
                    .withDatabaseName(
                            "board_integration_test"
                    )
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void registerDynamicProperties(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "app.file.local.root-path",
                () -> TEST_UPLOAD_ROOT.toString()
        );

        registry.add(
                "app.file.local.public-base-url",
                () -> "/files"
        );

        registry.add(
                "jwt.secret",
                () -> TEST_JWT_SECRET
        );

        registry.add(
                "jwt.access-token-expiration",
                () -> 3_600_000L
        );

        registry.add(
                "jwt.refresh-token-expiration",
                () -> 86_400_000L
        );
    }

    @BeforeEach
    void setUp() throws IOException {
        cleanDatabase();
        recreateUploadDirectory();
    }

    @AfterAll
    static void tearDownUploadDirectory()
            throws IOException {

        deleteRecursively(TEST_UPLOAD_ROOT);
    }

    @Test
    @DisplayName(
            "회원가입부터 게시글 삭제까지 전체 API 시나리오를 검증한다"
    )
    void completeBoardApiScenario()
            throws Exception {

        /*
         * 1. 테스트용 활성 카테고리 준비
         *
         * 카테고리 관리자 API까지 포함하려면 관리자 계정 생성이
         * 먼저 필요하므로, 이번 사용자 시나리오에서는
         * 카테고리만 Repository를 통해 사전 준비한다.
         */
        Category category =
                categoryRepository.saveAndFlush(
                        Category.create(
                                "통합 테스트"
                        )
                );

        Long categoryId =
                category.getId();

        /*
         * 2. 회원가입
         */
        ResponseEntity<String> signupResponse =
                postJson(
                        "/api/v1/auth/signup",
                        """
                                {
                                  "email": "integration@example.com",
                                  "password": "Password123!",
                                  "nickname": "integration"
                                }
                                """,
                        null
                );

        assertThat(
                signupResponse.getStatusCode()
        ).isEqualTo(HttpStatus.CREATED);

        JsonNode signupJson =
                parseBody(signupResponse);

        assertThat(
                signupJson.at("/success")
                        .asBoolean()
        ).isTrue();

        /*
         * 3. 로그인 및 Access Token 추출
         */
        ResponseEntity<String> loginResponse =
                postJson(
                        "/api/v1/auth/login",
                        """
                                {
                                  "email": "integration@example.com",
                                  "password": "Password123!"
                                }
                                """,
                        null
                );

        assertThat(
                loginResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        JsonNode loginJson =
                parseBody(loginResponse);

        String accessToken =
                loginJson
                        .at(
                                "/result/accessToken"
                        )
                        .asString();

        assertThat(accessToken)
                .isNotBlank();

        /*
         * 4. 게시글 작성
         */
        String createPostBody = """
                {
                  "categoryId": %d,
                  "title": "Testcontainers 통합 테스트",
                  "content": "실제 MySQL과 HTTP 요청을 검증합니다."
                }
                """.formatted(categoryId);

        ResponseEntity<String> createPostResponse =
                postJson(
                        "/api/v1/posts",
                        createPostBody,
                        accessToken
                );

        assertThat(
                createPostResponse.getStatusCode()
        ).isEqualTo(HttpStatus.CREATED);

        JsonNode createPostJson =
                parseBody(createPostResponse);

        Long postId =
                createPostJson
                        .at("/result/postId")
                        .asLong();

        assertThat(postId)
                .isPositive();

        /*
         * 5. 댓글 작성
         */
        ResponseEntity<String> commentResponse =
                postJson(
                        "/api/v1/posts/"
                                + postId
                                + "/comments",
                        """
                                {
                                  "content": "통합 테스트 댓글입니다."
                                }
                                """,
                        accessToken
                );

        assertThat(
                commentResponse.getStatusCode()
        ).isEqualTo(HttpStatus.CREATED);

        JsonNode commentJson =
                parseBody(commentResponse);

        Long commentId =
                commentJson
                        .at("/result/commentId")
                        .asLong();

        assertThat(commentId)
                .isPositive();

        /*
         * 6. 게시글 좋아요
         */
        ResponseEntity<String> likeResponse =
                exchangeWithoutBody(
                        "/api/v1/posts/"
                                + postId
                                + "/likes",
                        HttpMethod.PUT,
                        accessToken
                );

        assertThat(
                likeResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        JsonNode likeJson =
                parseBody(likeResponse);

        assertThat(
                likeJson.at("/result/liked")
                        .asBoolean()
        ).isTrue();

        assertThat(
                likeJson.at("/result/likeCount")
                        .asLong()
        ).isEqualTo(1L);

        /*
         * 7. multipart 이미지 업로드
         */
        ResponseEntity<String> imageUploadResponse =
                uploadPngImage(
                        postId,
                        accessToken
                );

        assertThat(
                imageUploadResponse.getStatusCode()
        ).isEqualTo(HttpStatus.CREATED);

        JsonNode imageUploadJson =
                parseBody(imageUploadResponse);

        Long imageId =
                imageUploadJson
                        .at(
                                "/result/images/0/imageId"
                        )
                        .asLong();

        String imageUrl =
                imageUploadJson
                        .at(
                                "/result/images/0/imageUrl"
                        )
                        .asString();

        assertThat(imageId)
                .isPositive();

        assertThat(imageUrl)
                .startsWith("/files/posts/");

        /*
         * 8. 실제 이미지 파일 HTTP 조회
         */
        ResponseEntity<byte[]> imageDownloadResponse =
                restTemplate.getForEntity(
                        imageUrl,
                        byte[].class
                );

        assertThat(
                imageDownloadResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        assertThat(
                imageDownloadResponse.getBody()
        ).containsExactly(TEST_PNG_BYTES);

        /*
         * 9. 게시글 상세 조회
         *
         * 인증 토큰을 같이 보내 likedByMe=true도 검증한다.
         */
        ResponseEntity<String> detailResponse =
                getJson(
                        "/api/v1/posts/" + postId,
                        accessToken
                );

        assertThat(
                detailResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        JsonNode detailJson =
                parseBody(detailResponse);

        assertThat(
                detailJson.at("/result/postId")
                        .asLong()
        ).isEqualTo(postId);

        assertThat(
                detailJson.at("/result/commentCount")
                        .asLong()
        ).isEqualTo(1L);

        assertThat(
                detailJson.at("/result/likeCount")
                        .asLong()
        ).isEqualTo(1L);

        assertThat(
                detailJson.at("/result/likedByMe")
                        .asBoolean()
        ).isTrue();

        assertThat(
                detailJson.at("/result/images")
                        .size()
        ).isEqualTo(1);

        assertThat(
                detailJson
                        .at(
                                "/result/images/0/imageId"
                        )
                        .asLong()
        ).isEqualTo(imageId);

        /*
         * 10. 게시글 목록 검색
         */
        ResponseEntity<String> listResponse =
                getJson(
                        "/api/v1/posts"
                                + "?keyword=Testcontainers"
                                + "&sort=latest"
                                + "&page=0"
                                + "&size=20",
                        null
                );

        assertThat(
                listResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        JsonNode listJson =
                parseBody(listResponse);

        assertThat(
                listJson.at("/result/totalElements")
                        .asLong()
        ).isEqualTo(1L);

        assertThat(
                listJson
                        .at(
                                "/result/posts/0/postId"
                        )
                        .asLong()
        ).isEqualTo(postId);

        assertThat(
                listJson
                        .at(
                                "/result/posts/0/likeCount"
                        )
                        .asLong()
        ).isEqualTo(1L);

        /*
         * 11. DB에 저장된 이미지 key와 실제 파일 확인
         */
        PostImage savedImage =
                postImageRepository
                        .findByIdAndPostId(
                                imageId,
                                postId
                        )
                        .orElseThrow();

        String storageKey =
                savedImage.getStorageKey();

        Path storedFilePath =
                TEST_UPLOAD_ROOT
                        .resolve(storageKey)
                        .normalize();

        assertThat(storedFilePath)
                .startsWith(TEST_UPLOAD_ROOT);

        assertThat(storedFilePath)
                .exists();

        /*
         * 12. 게시글 삭제
         *
         * 댓글 논리 삭제
         * 좋아요 물리 삭제
         * 이미지 메타데이터 삭제
         * 로컬 파일 삭제
         * 게시글 논리 삭제
         */
        ResponseEntity<String> deletePostResponse =
                exchangeWithoutBody(
                        "/api/v1/posts/" + postId,
                        HttpMethod.DELETE,
                        accessToken
                );

        assertThat(
                deletePostResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        /*
         * 13. 삭제된 게시글 상세 조회 불가
         */
        ResponseEntity<String> deletedDetailResponse =
                getJson(
                        "/api/v1/posts/" + postId,
                        null
                );

        assertThat(
                deletedDetailResponse.getStatusCode()
        ).isEqualTo(HttpStatus.NOT_FOUND);

        JsonNode deletedDetailJson =
                parseBody(deletedDetailResponse);

        assertThat(
                deletedDetailJson.at("/code")
                        .asString()
        ).isEqualTo("POST404");

        /*
         * 14. MySQL 연관 데이터 정리 검증
         */
        assertThat(
                queryString(
                        """
                        select status
                        from post
                        where id = ?
                        """,
                        postId
                )
        ).isEqualTo("DELETED");

        assertThat(
                queryCount(
                        """
                        select count(*)
                        from comment
                        where post_id = ?
                          and status = 'DELETED'
                        """,
                        postId
                )
        ).isEqualTo(1L);

        assertThat(
                queryCount(
                        """
                        select count(*)
                        from comment
                        where post_id = ?
                          and status = 'PUBLISHED'
                        """,
                        postId
                )
        ).isZero();

        assertThat(
                queryCount(
                        """
                        select count(*)
                        from post_like
                        where post_id = ?
                        """,
                        postId
                )
        ).isZero();

        assertThat(
                queryCount(
                        """
                        select count(*)
                        from post_image
                        where post_id = ?
                        """,
                        postId
                )
        ).isZero();

        /*
         * AFTER_COMMIT Listener가 동기적으로 실행되므로
         * 응답이 돌아온 시점에는 삭제 작업도 처리되어야 한다.
         */
        assertThat(
                queryCount(
                        """
                        select count(*)
                        from image_delete_task
                        """
                )
        ).isZero();

        /*
         * 15. 실제 로컬 파일도 삭제되었는지 검증
         */
        assertThat(storedFilePath)
                .doesNotExist();
    }

    @Test
    @DisplayName(
            "인증 없이 게시글 작성 요청을 보내면 401을 반환한다"
    )
    void createPostWithoutAuthenticationReturns401() {
        ResponseEntity<String> response =
                postJson(
                        "/api/v1/posts",
                        """
                                {
                                  "categoryId": 1,
                                  "title": "인증 없는 요청",
                                  "content": "실패해야 합니다."
                                }
                                """,
                        null
                );

        assertThat(
                response.getStatusCode()
        ).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<String> postJson(
            String path,
            String body,
            String accessToken
    ) {
        HttpHeaders headers =
                createHeaders(accessToken);

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.setAccept(
                List.of(
                        MediaType.APPLICATION_JSON
                )
        );

        HttpEntity<String> request =
                new HttpEntity<>(
                        body,
                        headers
                );

        return restTemplate.exchange(
                path,
                HttpMethod.POST,
                request,
                String.class
        );
    }

    private ResponseEntity<String> getJson(
            String path,
            String accessToken
    ) {
        HttpHeaders headers =
                createHeaders(accessToken);

        headers.setAccept(
                List.of(
                        MediaType.APPLICATION_JSON
                )
        );

        HttpEntity<Void> request =
                new HttpEntity<>(headers);

        return restTemplate.exchange(
                path,
                HttpMethod.GET,
                request,
                String.class
        );
    }

    private ResponseEntity<String>
    exchangeWithoutBody(
            String path,
            HttpMethod method,
            String accessToken
    ) {
        HttpHeaders headers =
                createHeaders(accessToken);

        headers.setAccept(
                List.of(
                        MediaType.APPLICATION_JSON
                )
        );

        HttpEntity<Void> request =
                new HttpEntity<>(headers);

        return restTemplate.exchange(
                path,
                method,
                request,
                String.class
        );
    }

    private ResponseEntity<String> uploadPngImage(
            Long postId,
            String accessToken
    ) {
        ByteArrayResource resource =
                new ByteArrayResource(
                        TEST_PNG_BYTES
                ) {
                    @Override
                    public String getFilename() {
                        return "integration-test.png";
                    }
                };

        HttpHeaders fileHeaders =
                new HttpHeaders();

        fileHeaders.setContentType(
                MediaType.IMAGE_PNG
        );

        HttpEntity<ByteArrayResource> filePart =
                new HttpEntity<>(
                        resource,
                        fileHeaders
                );

        MultiValueMap<String, Object> body =
                new LinkedMultiValueMap<>();

        body.add(
                "files",
                filePart
        );

        HttpHeaders requestHeaders =
                createHeaders(accessToken);

        requestHeaders.setContentType(
                MediaType.MULTIPART_FORM_DATA
        );

        requestHeaders.setAccept(
                List.of(
                        MediaType.APPLICATION_JSON
                )
        );

        HttpEntity<
                MultiValueMap<String, Object>>
                request =
                new HttpEntity<>(
                        body,
                        requestHeaders
                );

        return restTemplate.exchange(
                "/api/v1/posts/"
                        + postId
                        + "/images",
                HttpMethod.POST,
                request,
                String.class
        );
    }

    private HttpHeaders createHeaders(
            String accessToken
    ) {
        HttpHeaders headers =
                new HttpHeaders();

        if (accessToken != null
                && !accessToken.isBlank()) {

            headers.setBearerAuth(accessToken);
        }

        return headers;
    }

    private JsonNode parseBody(
            ResponseEntity<String> response
    ) throws Exception {
        assertThat(response.getBody())
                .isNotBlank();

        return jsonMapper.readTree(
                response.getBody()
        );
    }

    private long queryCount(
            String sql,
            Object... arguments
    ) {
        Long result =
                jdbcTemplate.queryForObject(
                        sql,
                        Long.class,
                        arguments
                );

        return Objects.requireNonNull(result);
    }

    private String queryString(
            String sql,
            Object... arguments
    ) {
        return jdbcTemplate.queryForObject(
                sql,
                String.class,
                arguments
        );
    }

    private void cleanDatabase() {
        jdbcTemplate.execute(
                "SET FOREIGN_KEY_CHECKS = 0"
        );

        try {
            truncateTable(
                    "image_delete_task"
            );

            truncateTable("post_image");
            truncateTable("post_like");
            truncateTable("comment");
            truncateTable("refresh_token");
            truncateTable("post");
            truncateTable("category");
            truncateTable("member");

        } finally {
            jdbcTemplate.execute(
                    "SET FOREIGN_KEY_CHECKS = 1"
            );
        }
    }

    private void truncateTable(
            String tableName
    ) {
        jdbcTemplate.execute(
                "TRUNCATE TABLE " + tableName
        );
    }

    private void recreateUploadDirectory()
            throws IOException {

        deleteRecursively(TEST_UPLOAD_ROOT);

        Files.createDirectories(
                TEST_UPLOAD_ROOT
        );
    }

    private static Path createTestUploadRoot() {
        try {
            return Files.createTempDirectory(
                            "board-integration-uploads-"
                    )
                    .toAbsolutePath()
                    .normalize();

        } catch (IOException exception) {
            throw new ExceptionInInitializerError(
                    exception
            );
        }
    }

    private static void deleteRecursively(
            Path root
    ) throws IOException {
        if (root == null
                || Files.notExists(root)) {
            return;
        }

        try (
                Stream<Path> paths =
                        Files.walk(root)
        ) {
            paths
                    .sorted(
                            java.util.Comparator
                                    .reverseOrder()
                    )
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);

                        } catch (
                                IOException exception
                        ) {
                            throw new IllegalStateException(
                                    "테스트 파일 삭제 실패: "
                                            + path,
                                    exception
                            );
                        }
                    });
        }
    }
}