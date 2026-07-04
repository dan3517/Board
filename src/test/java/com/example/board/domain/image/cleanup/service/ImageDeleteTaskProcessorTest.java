package com.example.board.domain.image.cleanup.service;

import com.example.board.domain.image.cleanup.entity.ImageDeleteTask;
import com.example.board.domain.image.cleanup.repository.ImageDeleteTaskRepository;
import com.example.board.domain.image.storage.ImageStorage;
import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ImageDeleteTaskProcessorTest {

    @Mock
    private ImageDeleteTaskRepository
            imageDeleteTaskRepository;

    @Mock
    private ImageStorage imageStorage;

    private ImageDeleteTaskProcessor
            imageDeleteTaskProcessor;

    @BeforeEach
    void setUp() {
        imageDeleteTaskProcessor =
                new ImageDeleteTaskProcessor(
                        imageDeleteTaskRepository,
                        imageStorage
                );
    }

    @Test
    @DisplayName("이미지 저장소 삭제에 성공하면 삭제 작업을 제거한다")
    void processSuccess() {
        // given
        ImageDeleteTask firstTask =
                createTask(
                        1L,
                        "posts/1/first.png"
                );

        ImageDeleteTask secondTask =
                createTask(
                        2L,
                        "posts/1/second.jpg"
                );

        given(
                imageDeleteTaskRepository
                        .findAllByIdInForUpdate(
                                List.of(1L, 2L)
                        )
        ).willReturn(
                List.of(
                        firstTask,
                        secondTask
                )
        );

        // when
        imageDeleteTaskProcessor.process(
                List.of(1L, 2L)
        );

        // then
        then(imageStorage)
                .should()
                .deleteAll(
                        List.of(
                                "posts/1/first.png",
                                "posts/1/second.jpg"
                        )
                );

        then(imageDeleteTaskRepository)
                .should()
                .deleteAllInBatch(
                        List.of(
                                firstTask,
                                secondTask
                        )
                );

        then(imageDeleteTaskRepository)
                .should()
                .flush();
    }

    @Test
    @DisplayName("이미지 저장소 삭제가 실패하면 작업을 유지하고 재시도 횟수를 증가시킨다")
    void processRecordsFailure() {
        // given
        ImageDeleteTask task =
                createTask(
                        1L,
                        "posts/1/failed.png"
                );

        given(
                imageDeleteTaskRepository
                        .findAllByIdInForUpdate(
                                List.of(1L)
                        )
        ).willReturn(
                List.of(task)
        );

        BusinessException storageException =
                new BusinessException(
                        ErrorCode.IMAGE_STORAGE_ERROR
                );

        willThrow(storageException)
                .given(imageStorage)
                .deleteAll(
                        List.of(
                                "posts/1/failed.png"
                        )
                );

        // when
        imageDeleteTaskProcessor.process(
                List.of(1L)
        );

        // then
        assertThat(task.getRetryCount())
                .isEqualTo(1);

        assertThat(task.getLastError())
                .isEqualTo(
                        ErrorCode
                                .IMAGE_STORAGE_ERROR
                                .getMessage()
                );

        then(imageDeleteTaskRepository)
                .should(never())
                .deleteAllInBatch(anyList());
    }

    @Test
    @DisplayName("이미 처리된 작업이면 저장소 삭제를 호출하지 않는다")
    void processDoesNothingWhenTaskMissing() {
        // given
        given(
                imageDeleteTaskRepository
                        .findAllByIdInForUpdate(
                                List.of(1L)
                        )
        ).willReturn(List.of());

        // when
        imageDeleteTaskProcessor.process(
                List.of(1L)
        );

        // then
        then(imageStorage)
                .shouldHaveNoInteractions();
    }

    private ImageDeleteTask createTask(
            Long id,
            String storageKey
    ) {
        ImageDeleteTask task =
                ImageDeleteTask.create(
                        storageKey
                );

        ReflectionTestUtils.setField(
                task,
                "id",
                id
        );

        return task;
    }
}