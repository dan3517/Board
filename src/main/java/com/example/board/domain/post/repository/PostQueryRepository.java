package com.example.board.domain.post.repository;

import com.example.board.domain.post.dto.query.PostListQueryDto;
import com.example.board.domain.post.dto.query.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryRepository {

    Page<PostListQueryDto> searchPosts(
            PostSearchCondition condition,
            Pageable pageable
    );
}