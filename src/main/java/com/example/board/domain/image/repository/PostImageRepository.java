package com.example.board.domain.image.repository;

import com.example.board.domain.image.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostImageRepository
        extends JpaRepository<PostImage, Long> {

    long countByPostId(Long postId);

    List<PostImage>
    findAllByPostIdOrderBySortOrderAsc(
            Long postId
    );

    Optional<PostImage> findByIdAndPostId(
            Long imageId,
            Long postId
    );

    @Query("""
            select max(postImage.sortOrder)
            from PostImage postImage
            where postImage.post.id = :postId
            """)
    Integer findMaxSortOrderByPostId(
            @Param("postId")
            Long postId
    );
}