package com.example.board.domain.image.cleanup.event;

import java.util.List;

public record ImageDeleteRequestedEvent(
        List<Long> taskIds
) {

    public ImageDeleteRequestedEvent {
        taskIds = List.copyOf(taskIds);
    }
}