package com.example.board.global.exception;

import com.example.board.global.common.response.ApiResponse;
import com.example.board.global.security.JwtAuthenticationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        ErrorCode errorCode = ErrorCode.INVALID_INPUT;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode, errors));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception
    ) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
            Exception exception
    ) {
        log.error("Unhandled exception occurred", exception);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleDataIntegrityViolationException(
            DataIntegrityViolationException exception
    ) {
        log.warn(
                "Database integrity constraint violation",
                exception
        );

        ErrorCode errorCode =
                ErrorCode.DATA_INTEGRITY_VIOLATION;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode));
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleJwtAuthenticationException(
            JwtAuthenticationException exception
    ) {
        ErrorCode errorCode =
                exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception
    ) {
        log.debug(
                "Request body could not be read",
                exception
        );

        ErrorCode errorCode =
                ErrorCode.INVALID_INPUT;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception
    ) {
        ErrorCode errorCode =
                ErrorCode.INVALID_INPUT;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>>
    handleConstraintViolationException(
            ConstraintViolationException exception
    ) {
        Map<String, String> errors =
                new LinkedHashMap<>();

        for (ConstraintViolation<?> violation
                : exception.getConstraintViolations()) {

            errors.putIfAbsent(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
            );
        }

        ErrorCode errorCode =
                ErrorCode.INVALID_INPUT;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(
                        ApiResponse.failure(
                                errorCode,
                                errors
                        )
                );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleHandlerMethodValidationException(
            HandlerMethodValidationException exception
    ) {
        ErrorCode errorCode =
                ErrorCode.INVALID_INPUT;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode));
    }

}