package com.example.board.domain.image.cleanup.listener;

import com.example.board.domain.image.cleanup.event.ImageDeleteRequestedEvent;
import com.example.board.domain.image.cleanup.service.ImageDeleteTaskProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ImageDeleteEventListener {

    private final ImageDeleteTaskProcessor
            imageDeleteTaskProcessor;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            ImageDeleteRequestedEvent event
    ) {
        imageDeleteTaskProcessor.process(
                event.taskIds()
        );
    }
}