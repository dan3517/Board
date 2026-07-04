package com.example.board.domain.comment.repository;

import com.example.board.domain.comment.dto.query.CommentListQueryDto;
import com.example.board.domain.comment.entity.Comment;
import com.example.board.domain.comment.entity.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository
        extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndStatus(
            Long id,
            CommentStatus status
    );

    long countByPostIdAndStatus(
            Long postId,
            CommentStatus status
    );

    @Query(
            value = """
                    select new com.example.board.domain.comment.dto.query.CommentListQueryDto(
                        comment.id,
                        comment.content,
                        author.id,
                        author.nickname,
                        comment.createdAt,
                        comment.updatedAt
                    )
                    from Comment comment
                    join comment.author author
                    where comment.post.id = :postId
                      and comment.status = :status
                    order by comment.createdAt asc,
                             comment.id asc
                    """,
            countQuery = """
                    select count(comment.id)
                    from Comment comment
                    where comment.post.id = :postId
                      and comment.status = :status
                    """
    )
    Page<CommentListQueryDto> findCommentsByPostId(
            @Param("postId") Long postId,
            @Param("status") CommentStatus status,
            Pageable pageable
    );

    @Modifying(
            flushAutomatically = true
    )
    @Query("""
        update Comment comment
        set comment.status = :deletedStatus,
            comment.updatedAt = CURRENT_TIMESTAMP
        where comment.post.id = :postId
          and comment.status = :publishedStatus
        """)
    int softDeleteAllByPostId(
            @Param("postId")
            Long postId,

            @Param("publishedStatus")
            CommentStatus publishedStatus,

            @Param("deletedStatus")
            CommentStatus deletedStatus
    );
}