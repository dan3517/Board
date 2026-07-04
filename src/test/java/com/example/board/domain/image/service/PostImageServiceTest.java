package com.example.board.domain.image.service;

import com.example.board.domain.category.entity.Category;
import com.example.board.domain.image.cleanup.service.ImageDeleteTaskCoordinator;
import com.example.board.domain.image.dto.response.PostImageUploadResponse;
import com.example.board.domain.image.entity.PostImage;
import com.example.board.domain.image.repository.PostImageRepository;
import com.example.board.domain.image.storage.ImageStorage;
import com.example.board.domain.image.validation.ImageFileValidator;
import com.example.board.domain.image.validation.ImageType;
import com.example.board.domain.image.validation.ValidatedImage;
import com.example.board.domain.member.entity.Member;
import com.example.board.domain.member.entity.MemberRole;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.entity.PostStatus;
import com.example.board.domain.post.repository.PostRepository;
import com.example.board.global.config.properties.ImageProperties;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PostImageServiceTest {

    @Mock
    private PostImageRepository postImageRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ImageStorage imageStorage;

    @Mock
    private ImageFileValidator imageFileValidator;

    @Mock
    private ImageDeleteTaskCoordinator imageDeleteTaskCoordinator;

    private PostImageService postImageService;

    @BeforeEach
    void setUp() {
        postImageService =
                new PostImageService(
                        postImageRepository,
                        postRepository,
                        imageStorage,
                        imageFileValidator,
                        new ImageProperties(
                                5,
                                10 * 1024 * 1024
                        ),
                        imageDeleteTaskCoordinator
                );
    }

    @Test
    @DisplayName("게시글 작성자가 이미지를 업로드한다")
    void uploadImagesSuccess() {
        // given
        Long memberId = 1L;
        Long postId = 10L;

        Post post =
                createPost(
                        memberId,
                        postId
                );

        MockMultipartFile firstFile =
                new MockMultipartFile(
                        "files",
                        "first.png",
                        "image/png",
                        pngBytes()
                );

        MockMultipartFile secondFile =
                new MockMultipartFile(
                        "files",
                        "second.jpg",
                        "image/jpeg",
                        jpegBytes()
                );

        List<MultipartFile> files =
                List.of(
                        firstFile,
                        secondFile
                );

        given(
                postRepository
                        .findByIdAndStatusForUpdate(
                                postId,
                                PostStatus.PUBLISHED
                        )
        ).willReturn(Optional.of(post));

        given(
                postImageRepository
                        .countByPostId(postId)
        ).willReturn(1L);

        given(
                postImageRepository
                        .findMaxSortOrderByPostId(
                                postId
                        )
        ).willReturn(0);

        given(
                imageFileValidator.validate(
                        firstFile
                )
        ).willReturn(
                new ValidatedImage(
                        "first.png",
                        ImageType.PNG
                )
        );

        given(
                imageFileValidator.validate(
                        secondFile
                )
        ).willReturn(
                new ValidatedImage(
                        "second.jpg",
                        ImageType.JPEG
                )
        );

        given(
                imageStorage.store(
                        postId,
                        firstFile,
                        ImageType.PNG
                )
        ).willReturn(
                "posts/10/first-uuid.png"
        );

        given(
                imageStorage.store(
                        postId,
                        secondFile,
                        ImageType.JPEG
                )
        ).willReturn(
                "posts/10/second-uuid.jpg"
        );

        AtomicLong imageId =
                new AtomicLong(100L);

        given(
                postImageRepository
                        .saveAllAndFlush(anyList())
        ).willAnswer(invocation -> {
            List<PostImage> images =
                    invocation.getArgument(0);

            for (PostImage image : images) {
                ReflectionTestUtils.setField(
                        image,
                        "id",
                        imageId.getAndIncrement()
                );
            }

            return images;
        });

        given(
                imageStorage.getUrl(
                        "posts/10/first-uuid.png"
                )
        ).willReturn(
                "http://localhost:8080/files/"
                        + "posts/10/first-uuid.png"
        );

        given(
                imageStorage.getUrl(
                        "posts/10/second-uuid.jpg"
                )
        ).willReturn(
                "http://localhost:8080/files/"
                        + "posts/10/second-uuid.jpg"
        );

        // when
        PostImageUploadResponse response =
                postImageService.uploadImages(
                        memberId,
                        MemberRole.USER,
                        postId,
                        files
                );

        // then
        assertThat(response.images())
                .hasSize(2);

        assertThat(
                response.images()
                        .getFirst()
                        .imageId()
        ).isEqualTo(100L);

        assertThat(
                response.images()
                        .getFirst()
                        .sortOrder()
        ).isEqualTo(1);

        assertThat(
                response.images()
                        .get(1)
                        .sortOrder()
        ).isEqualTo(2);

        then(postImageRepository)
                .should()
                .saveAllAndFlush(anyList());
    }

    @Test
    @DisplayName("게시글 이미지가 최대 개수를 초과하면 실패한다")
    void uploadImagesFailsWhenCountExceeded() {
        // given
        Long memberId = 1L;
        Long postId = 10L;

        Post post =
                createPost(
                        memberId,
                        postId
                );

        List<MultipartFile> files =
                List.of(
                        imageFile("one.png"),
                        imageFile("two.png")
                );

        given(
                postRepository
                        .findByIdAndStatusForUpdate(
                                postId,
                                PostStatus.PUBLISHED
                        )
        ).willReturn(Optional.of(post));

        given(
                postImageRepository
                        .countByPostId(postId)
        ).willReturn(4L);

        // when
        Throwable throwable =
                catchThrowable(
                        () ->
                                postImageService
                                        .uploadImages(
                                                memberId,
                                                MemberRole.USER,
                                                postId,
                                                files
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
                        ErrorCode
                                .IMAGE_COUNT_EXCEEDED
                );

        then(imageStorage)
                .shouldHaveNoInteractions();

        then(postImageRepository)
                .should(never())
                .saveAllAndFlush(anyList());
    }

    @Test
    @DisplayName("다른 회원은 게시글 이미지를 업로드할 수 없다")
    void uploadImagesFailsWhenNotAuthor() {
        // given
        Long authorId = 1L;
        Long otherMemberId = 2L;
        Long postId = 10L;

        Post post =
                createPost(
                        authorId,
                        postId
                );

        given(
                postRepository
                        .findByIdAndStatusForUpdate(
                                postId,
                                PostStatus.PUBLISHED
                        )
        ).willReturn(Optional.of(post));

        // when
        Throwable throwable =
                catchThrowable(
                        () ->
                                postImageService
                                        .uploadImages(
                                                otherMemberId,
                                                MemberRole.USER,
                                                postId,
                                                List.of(
                                                        imageFile(
                                                                "image.png"
                                                        )
                                                )
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
                        ErrorCode.POST_ACCESS_DENIED
                );

        then(imageStorage)
                .shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("이미지 삭제 시 저장소 삭제 작업을 생성한다")
    void deleteImageEnqueuesDeleteTask() {
        // given
        Long memberId = 1L;
        Long postId = 10L;
        Long imageId = 100L;

        Post post =
                createPost(
                        memberId,
                        postId
                );

        PostImage image =
                PostImage.create(
                        post,
                        "image.png",
                        "posts/10/test.png",
                        "image/png",
                        1000L,
                        0
                );

        ReflectionTestUtils.setField(
                image,
                "id",
                imageId
        );

        given(
                postRepository
                        .findByIdAndStatusForUpdate(
                                postId,
                                PostStatus.PUBLISHED
                        )
        ).willReturn(Optional.of(post));

        given(
                postImageRepository
                        .findByIdAndPostId(
                                imageId,
                                postId
                        )
        ).willReturn(Optional.of(image));

        // when
        postImageService.deleteImage(
                memberId,
                MemberRole.USER,
                postId,
                imageId
        );

        // then
        then(imageDeleteTaskCoordinator)
                .should()
                .enqueue(List.of(image));

        then(imageStorage)
                .should(never())
                .delete(any(String.class));
    }

    private Post createPost(
            Long authorId,
            Long postId
    ) {
        Member author = Member.create(
                "author@example.com",
                "encoded-password",
                "author"
        );

        ReflectionTestUtils.setField(
                author,
                "id",
                authorId
        );

        Category category =
                Category.create("자유");

        Post post = Post.create(
                author,
                category,
                "게시글 제목",
                "게시글 내용"
        );

        ReflectionTestUtils.setField(
                post,
                "id",
                postId
        );

        return post;
    }

    private MockMultipartFile imageFile(
            String filename
    ) {
        return new MockMultipartFile(
                "files",
                filename,
                "image/png",
                pngBytes()
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

    private byte[] jpegBytes() {
        return new byte[]{
                (byte) 0xFF,
                (byte) 0xD8,
                (byte) 0xFF,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00
        };
    }
}