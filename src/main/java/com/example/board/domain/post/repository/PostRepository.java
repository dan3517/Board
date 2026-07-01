package com.example.board.domain.post.repository;

import com.example.board.domain.post.dto.query.PostDetailQueryDto;
import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository
        extends JpaRepository<Post, Long>,
        PostQueryRepository {

    Optional<Post> findByIdAndStatus(
            Long id,
            PostStatus status
    );

    boolean existsByIdAndStatus(
            Long id,
            PostStatus status
    );

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
            update Post post
            set post.viewCount = post.viewCount + 1
            where post.id = :postId
              and post.status = :status
            """)
    int increaseViewCount(
            @Param("postId") Long postId,
            @Param("status") PostStatus status
    );

    @Query("""
            select new com.example.board.domain.post.dto.query.PostDetailQueryDto(
                post.id,
                post.title,
                post.content,
                post.viewCount,
                post.createdAt,
                post.updatedAt,
                author.id,
                author.nickname,
                category.id,
                category.name
            )
            from Post post
            join post.author author
            join post.category category
            where post.id = :postId
              and post.status = :status
            """)
    Optional<PostDetailQueryDto> findDetailByIdAndStatus(
            @Param("postId") Long postId,
            @Param("status") PostStatus status
    );
}