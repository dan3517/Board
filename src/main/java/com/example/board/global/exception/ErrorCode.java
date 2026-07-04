package com.example.board.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(
            HttpStatus.BAD_REQUEST,
            "COMMON400",
            "입력값이 올바르지 않습니다."
    ),

    UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "COMMON401",
            "인증이 필요합니다."
    ),

    FORBIDDEN(
            HttpStatus.FORBIDDEN,
            "COMMON403",
            "요청을 수행할 권한이 없습니다."
    ),

    NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "COMMON404",
            "요청한 리소스를 찾을 수 없습니다."
    ),

    METHOD_NOT_ALLOWED(
            HttpStatus.METHOD_NOT_ALLOWED,
            "COMMON405",
            "지원하지 않는 HTTP 메서드입니다."
    ),

    DATA_INTEGRITY_VIOLATION(
            HttpStatus.CONFLICT,
            "COMMON409",
            "데이터 제약조건을 위반했습니다."
    ),

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500",
            "서버 내부 오류가 발생했습니다."
    ),

    INVALID_LOGIN(
            HttpStatus.UNAUTHORIZED,
            "AUTH4011",
            "이메일 또는 비밀번호가 올바르지 않습니다."
    ),

    INVALID_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH4012",
            "유효하지 않은 토큰입니다."
    ),

    EXPIRED_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH4013",
            "만료된 토큰입니다."
    ),


    INVALID_REFRESH_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH4014",
            "유효하지 않은 리프레시 토큰입니다."
    ),

    EXPIRED_REFRESH_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH4015",
            "만료된 리프레시 토큰입니다."
    ),

    MEMBER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "MEMBER404",
            "회원을 찾을 수 없습니다."
    ),

    DUPLICATE_EMAIL(
            HttpStatus.CONFLICT,
            "MEMBER4091",
            "이미 사용 중인 이메일입니다."
    ),

    DUPLICATE_NICKNAME(
            HttpStatus.CONFLICT,
            "MEMBER4092",
            "이미 사용 중인 닉네임입니다."
    ),

    DUPLICATE_MEMBER_DATA(
            HttpStatus.CONFLICT,
            "이미 사용 중인 회원 정보입니다.",
            "MEMBER409"
    ),

    CATEGORY_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CATEGORY404",
            "카테고리를 찾을 수 없습니다."
    ),

    CATEGORY_NOT_AVAILABLE(
            HttpStatus.BAD_REQUEST,
            "CATEGORY400",
            "현재 사용할 수 없는 카테고리입니다."
    ),

    DUPLICATE_CATEGORY_NAME(
            HttpStatus.CONFLICT,
            "CATEGORY409",
            "이미 사용 중인 카테고리 이름입니다."
    ),

    INVALID_POST_SORT(
            HttpStatus.BAD_REQUEST,
            "POST4001",
            "지원하지 않는 게시글 정렬 방식입니다."
    ),

    POST_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "POST404",
            "게시글을 찾을 수 없습니다."
    ),

    POST_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "POST403",
            "게시글을 변경할 권한이 없습니다."
    ),

    COMMENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "COMMENT404",
            "댓글을 찾을 수 없습니다."
    ),

    COMMENT_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "COMMENT403",
            "댓글을 변경할 권한이 없습니다."
    ),

    IMAGE_FILE_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "IMAGE4001",
            "업로드할 이미지 파일이 필요합니다."
    ),

    IMAGE_TOO_LARGE(
            HttpStatus.PAYLOAD_TOO_LARGE,
            "IMAGE413",
            "이미지 파일 크기가 허용 범위를 초과했습니다."
    ),

    IMAGE_UNSUPPORTED_TYPE(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "IMAGE415",
            "지원하지 않는 이미지 형식입니다."
    ),

    IMAGE_INVALID_FILE(
            HttpStatus.BAD_REQUEST,
            "IMAGE4002",
            "유효하지 않은 이미지 파일입니다."
    ),

    IMAGE_COUNT_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "IMAGE4003",
            "게시글에 첨부할 수 있는 이미지 개수를 초과했습니다."
    ),

    IMAGE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "IMAGE404",
            "이미지를 찾을 수 없습니다."
    ),

    IMAGE_STORAGE_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "IMAGE500",
            "이미지 저장 중 오류가 발생했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
