package com.example.board.domain.post.dto.request;

import com.example.board.domain.post.dto.query.PostSearchCondition;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;

public record PostSearchRequest(

        @Parameter(
                description = "페이지 번호, 0부터 시작",
                example = "0"
        )
        @Min(
                value = 0,
                message = "페이지 번호는 0 이상이어야 합니다."
        )
        Integer page,

        @Parameter(
                description = "페이지 크기, 최대 100",
                example = "20"
        )
        @Min(
                value = 1,
                message = "페이지 크기는 1 이상이어야 합니다."
        )
        @Max(
                value = 100,
                message = "페이지 크기는 100 이하여야 합니다."
        )
        Integer size,

        @Parameter(
                description = "제목 또는 본문 검색어",
                example = "querydsl"
        )
        @Size(
                max = 100,
                message = "검색어는 100자 이하여야 합니다."
        )
        String keyword,

        @Parameter(
                description = "작성자 닉네임 검색어",
                example = "backend"
        )
        @Size(
                max = 20,
                message = "작성자 검색어는 20자 이하여야 합니다."
        )
        String author,

        @Parameter(
                description = "카테고리 ID",
                example = "4"
        )
        @Positive(
                message = "카테고리 ID는 양수여야 합니다."
        )
        Long categoryId,

        @Parameter(
                description = """
                        정렬 조건
                        - latest: 최신순
                        - views: 조회순
                        - likes: 좋아요순
                        """,
                example = "latest"
        )
        @Size(
                max = 20,
                message = "정렬 조건은 20자 이하여야 합니다."
        )
        String sort
) {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public int resolvedPage() {
        return page == null
                ? DEFAULT_PAGE
                : page;
    }

    public int resolvedSize() {
        return size == null
                ? DEFAULT_SIZE
                : size;
    }

    public String normalizedKeyword() {
        return normalize(keyword);
    }

    public String normalizedAuthor() {
        return normalize(author);
    }

    public PostSortType resolvedSort() {
        return PostSortType.from(sort);
    }

    public PostSearchCondition toCondition() {
        return new PostSearchCondition(
                normalizedKeyword(),
                normalizedAuthor(),
                categoryId,
                resolvedSort()
        );
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.strip();
    }
}