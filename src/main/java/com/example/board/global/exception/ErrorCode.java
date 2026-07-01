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
            "사용할 수 있는 카테고리를 찾을 수 없습니다."
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
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
