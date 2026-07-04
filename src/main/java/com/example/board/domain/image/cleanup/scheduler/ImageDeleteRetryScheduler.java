package com.example.board.domain.image.cleanup.scheduler;

import com.example.board.domain.image.cleanup.service.ImageDeleteTaskProcessor;
import com.example.board.global.config.properties.ImageCleanupProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageDeleteRetryScheduler {

    private final ImageDeleteTaskProcessor
            imageDeleteTaskProcessor;

    private final ImageCleanupProperties
            imageCleanupProperties;

    @Scheduled(
            initialDelayString =
                    "${app.image.cleanup.initial-delay-ms:30000}",
            fixedDelayString =
                    "${app.image.cleanup.retry-delay-ms:300000}"
    )
    public void retryPendingTasks() {
        List<Long> taskIds =
                imageDeleteTaskProcessor
                        .findPendingTaskIds(
                                imageCleanupProperties
                                        .batchSize()
                        );

        if (taskIds.isEmpty()) {
            return;
        }

        log.info(
                "미처리 이미지 삭제 작업 재시도. "
                        + "taskCount={}",
                taskIds.size()
        );

        imageDeleteTaskProcessor.process(taskIds);
    }
}