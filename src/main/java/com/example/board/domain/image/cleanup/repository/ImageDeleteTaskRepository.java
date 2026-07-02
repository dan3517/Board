package com.example.board.domain.image.cleanup.repository;

import com.example.board.domain.image.cleanup.entity.ImageDeleteTask;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageDeleteTaskRepository
        extends JpaRepository<ImageDeleteTask, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select task
            from ImageDeleteTask task
            where task.id in :taskIds
            order by task.id asc
            """)
    List<ImageDeleteTask> findAllByIdInForUpdate(
            @Param("taskIds")
            List<Long> taskIds
    );

    @Query("""
            select task.id
            from ImageDeleteTask task
            order by task.createdAt asc,
                     task.id asc
            """)
    List<Long> findPendingTaskIds(
            Pageable pageable
    );
}