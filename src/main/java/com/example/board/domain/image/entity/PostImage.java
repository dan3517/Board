package com.example.board.domain.image.entity;

import com.example.board.domain.post.entity.Post;
import com.example.board.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "post_image",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_post_image_storage_key",
                        columnNames = "storage_key"
                ),
                @UniqueConstraint(
                        name = "uk_post_image_post_sort_order",
                        columnNames = {
                                "post_id",
                                "sort_order"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_post_image_post",
                        columnList = "post_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "post_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_post_image_post"
            )
    )
    private Post post;

    @Column(
            name = "original_filename",
            nullable = false,
            length = 255
    )
    private String originalFilename;

    @Column(
            name = "storage_key",
            nullable = false,
            length = 500
    )
    private String storageKey;

    @Column(
            name = "content_type",
            nullable = false,
            length = 100
    )
    private String contentType;

    @Column(
            name = "file_size",
            nullable = false
    )
    private long fileSize;

    @Column(
            name = "sort_order",
            nullable = false
    )
    private int sortOrder;

    private PostImage(
            Post post,
            String originalFilename,
            String storageKey,
            String contentType,
            long fileSize,
            int sortOrder
    ) {
        this.post = post;
        this.originalFilename = originalFilename;
        this.storageKey = storageKey;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.sortOrder = sortOrder;
    }

    public static PostImage create(
            Post post,
            String originalFilename,
            String storageKey,
            String contentType,
            long fileSize,
            int sortOrder
    ) {
        return new PostImage(
                post,
                originalFilename,
                storageKey,
                contentType,
                fileSize,
                sortOrder
        );
    }
}