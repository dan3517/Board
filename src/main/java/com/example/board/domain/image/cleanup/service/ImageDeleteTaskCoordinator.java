package com.example.board.domain.image.cleanup.service;

import com.example.board.domain.image.cleanup.entity.ImageDeleteTask;
import com.example.board.domain.image.cleanup.event.ImageDeleteRequestedEvent;
import com.example.board.domain.image.cleanup.repository.ImageDeleteTaskRepository;
import com.example.board.domain.image.entity.PostImage;
import com.example.board.domain.image.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageDeleteTaskCoordinator {

    private final ImageDeleteTaskRepository
            imageDeleteTaskRepository;

    private final PostImageRepository
            postImageRepository;

    private final ApplicationEventPublisher
            eventPublisher;

    @Transactional(
            propagation = Propagation.MANDATORY
    )
    public void enqueue(
            List<PostImage> images
    ) {
        if (images == null || images.isEmpty()) {
            return;
        }

        List<ImageDeleteTask> tasks =
                images.stream()
                        .map(PostImage::getStorageKey)
                        .map(ImageDeleteTask::create)
                        .toList();

        List<ImageDeleteTask> savedTasks =
                imageDeleteTaskRepository
                        .saveAllAndFlush(tasks);

        postImageRepository
                .deleteAllInBatch(images);

        postImageRepository.flush();

        List<Long> taskIds =
                savedTasks.stream()
                        .map(ImageDeleteTask::getId)
                        .toList();

        eventPublisher.publishEvent(
                new ImageDeleteRequestedEvent(
                        taskIds
                )
        );
    }
}