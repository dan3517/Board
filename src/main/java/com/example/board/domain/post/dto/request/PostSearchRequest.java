package com.example.board.domain.post.dto.request;

import com.example.board.domain.post.dto.query.PostSearchCondition;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;

public record PostSearchRequest(

        @Min(
                value = 0,
                message = "페이지 번호는 0 이상이어야 합니다."
        )
        Integer page,

        @Min(
                value = 1,
                message = "페이지 크기는 1 이상이어야 합니다."
        )
        @Max(
                value = 100,
                message = "페이지 크기는 100 이하여야 합니다."
        )
        Integer size,

        @Size(
                max = 100,
                message = "검색어는 100자 이하여야 합니다."
        )
        String keyword,

        @Size(
                max = 20,
                message = "작성자 검색어는 20자 이하여야 합니다."
        )
        String author,

        @Positive(
                message = "카테고리 ID는 양수여야 합니다."
        )
        Long categoryId,

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

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.strip();
    }

    public PostSearchCondition toCondition() {
        return new PostSearchCondition(
                normalizedKeyword(),
                normalizedAuthor(),
                categoryId,
                resolvedSort()
        );
    }
}