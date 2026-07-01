package com.example.board.domain.post.repository;

import com.example.board.domain.post.entity.Post;
import com.example.board.domain.post.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository
        extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndStatus(
            Long id,
            PostStatus status
    );
}