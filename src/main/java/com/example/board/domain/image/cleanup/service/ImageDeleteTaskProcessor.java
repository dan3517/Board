package com.example.board.domain.image.cleanup.service;

import com.example.board.domain.image.cleanup.entity.ImageDeleteTask;
import com.example.board.domain.image.cleanup.repository.ImageDeleteTaskRepository;
import com.example.board.domain.image.storage.ImageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageDeleteTaskProcessor {

    private final ImageDeleteTaskRepository
            imageDeleteTaskRepository;

    private final ImageStorage imageStorage;

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void process(
            List<Long> taskIds
    ) {
        if (taskIds == null || taskIds.isEmpty()) {
            return;
        }

        List<ImageDeleteTask> tasks =
                imageDeleteTaskRepository
                        .findAllByIdInForUpdate(
                                taskIds
                        );

        if (tasks.isEmpty()) {
            return;
        }

        List<String> storageKeys =
                tasks.stream()
                        .map(
                                ImageDeleteTask
                                        ::getStorageKey
                        )
                        .toList();

        try {
            imageStorage.deleteAll(storageKeys);

            imageDeleteTaskRepository
                    .deleteAllInBatch(tasks);

            imageDeleteTaskRepository.flush();

            log.info(
                    "이미지 삭제 작업 완료. "
                            + "taskCount={}",
                    tasks.size()
            );

        } catch (RuntimeException exception) {
            String errorMessage =
                    resolveErrorMessage(exception);

            tasks.forEach(task ->
                    task.recordFailure(
                            errorMessage
                    )
            );

            log.error(
                    "이미지 저장소 삭제 실패. "
                            + "taskIds={}, retryCount 증가",
                    taskIds,
                    exception
            );
        }
    }

    @Transactional(readOnly = true)
    public List<Long> findPendingTaskIds(
            int limit
    ) {
        if (limit <= 0) {
            throw new IllegalArgumentException(
                    "limit은 1 이상이어야 합니다."
            );
        }

        return imageDeleteTaskRepository
                .findPendingTaskIds(
                        PageRequest.of(
                                0,
                                limit
                        )
                );
    }

    private String resolveErrorMessage(
            RuntimeException exception
    ) {
        String message =
                exception.getMessage();

        if (StringUtils.hasText(message)) {
            return message;
        }

        return exception
                .getClass()
                .getSimpleName();
    }
}