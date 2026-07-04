package com.example.board.global.common.response;

import com.example.board.global.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.ALWAYS)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private boolean success;
    private String code;
    private String message;
    private T result;

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(
                true,
                "COMMON200",
                "요청에 성공했습니다.",
                result
        );
    }

    public static <T> ApiResponse<T> success(
            String code,
            String message,
            T result
    ) {
        return new ApiResponse<>(
                true,
                code,
                message,
                result
        );
    }

    public static ApiResponse<Void> failure(ErrorCode errorCode) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
    }

    public static <T> ApiResponse<T> failure(
            ErrorCode errorCode,
            T result
    ) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                result
        );
    }
}