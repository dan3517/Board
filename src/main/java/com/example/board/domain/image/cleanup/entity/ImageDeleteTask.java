package com.example.board.domain.image.cleanup.entity;

import com.example.board.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@Entity
@Table(
        name = "image_delete_task",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_image_delete_task_storage_key",
                        columnNames = "storage_key"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageDeleteTask extends BaseEntity {

    private static final int MAX_ERROR_LENGTH =
            1000;

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            name = "storage_key",
            nullable = false,
            length = 500
    )
    private String storageKey;

    @Column(
            name = "retry_count",
            nullable = false
    )
    private int retryCount;

    @Column(
            name = "last_error",
            length = MAX_ERROR_LENGTH
    )
    private String lastError;

    private ImageDeleteTask(
            String storageKey
    ) {
        this.storageKey = storageKey;
        this.retryCount = 0;
        this.lastError = null;
    }

    public static ImageDeleteTask create(
            String storageKey
    ) {
        if (!StringUtils.hasText(storageKey)) {
            throw new IllegalArgumentException(
                    "storageKey는 필수입니다."
            );
        }

        return new ImageDeleteTask(storageKey);
    }

    public void recordFailure(
            String errorMessage
    ) {
        this.retryCount++;

        if (!StringUtils.hasText(errorMessage)) {
            this.lastError =
                    "알 수 없는 이미지 삭제 오류";
            return;
        }

        String normalized =
                errorMessage.strip();

        this.lastError =
                normalized.length() <= MAX_ERROR_LENGTH
                        ? normalized
                        : normalized.substring(
                        0,
                        MAX_ERROR_LENGTH
                );
    }
}