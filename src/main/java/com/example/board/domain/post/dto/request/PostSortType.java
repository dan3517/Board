package com.example.board.domain.post.dto.request;

import com.example.board.global.exception.BusinessException;
import com.example.board.global.exception.ErrorCode;
import org.springframework.util.StringUtils;

import java.util.Locale;

public enum PostSortType {

    LATEST,
    VIEWS,
    LIKES;

    public static PostSortType from(String value) {
        if (!StringUtils.hasText(value)) {
            return LATEST;
        }

        try {
            return PostSortType.valueOf(
                    value.strip()
                            .toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(
                    ErrorCode.INVALID_POST_SORT
            );
        }
    }
}