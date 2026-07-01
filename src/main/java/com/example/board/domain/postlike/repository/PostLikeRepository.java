package com.example.board.domain.postlike.repository;

import com.example.board.domain.postlike.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository
        extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndMemberId(
            Long postId,
            Long memberId
    );

    long countByPostId(Long postId);

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
            delete from PostLike postLike
            where postLike.post.id = :postId
              and postLike.member.id = :memberId
            """)
    int deleteByPostIdAndMemberId(
            @Param("postId") Long postId,
            @Param("memberId") Long memberId
    );
}