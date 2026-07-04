package com.example.board.global.swagger;

public final class SwaggerExamples {

    public static final String INVALID_INPUT = """
            {
              "success": false,
              "code": "COMMON400",
              "message": "입력값이 올바르지 않습니다.",
              "result": null
            }
            """;

    public static final String VALIDATION_ERROR = """
            {
              "success": false,
              "code": "COMMON400",
              "message": "입력값이 올바르지 않습니다.",
              "result": {
                "email": "올바른 이메일 형식이어야 합니다.",
                "password": "비밀번호는 필수입니다."
              }
            }
            """;

    public static final String UNAUTHORIZED = """
            {
              "success": false,
              "code": "COMMON401",
              "message": "인증이 필요합니다.",
              "result": null
            }
            """;

    public static final String FORBIDDEN = """
            {
              "success": false,
              "code": "COMMON403",
              "message": "요청을 수행할 권한이 없습니다.",
              "result": null
            }
            """;

    public static final String MEMBER_NOT_FOUND = """
            {
              "success": false,
              "code": "MEMBER404",
              "message": "회원을 찾을 수 없습니다.",
              "result": null
            }
            """;

    public static final String DUPLICATE_EMAIL = """
            {
              "success": false,
              "code": "MEMBER4091",
              "message": "이미 사용 중인 이메일입니다.",
              "result": null
            }
            """;

    public static final String DUPLICATE_NICKNAME = """
            {
              "success": false,
              "code": "MEMBER4092",
              "message": "이미 사용 중인 닉네임입니다.",
              "result": null
            }
            """;

    public static final String INVALID_LOGIN = """
            {
              "success": false,
              "code": "AUTH401",
              "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
              "result": null
            }
            """;

    public static final String INVALID_REFRESH_TOKEN = """
            {
              "success": false,
              "code": "AUTH4013",
              "message": "유효하지 않은 Refresh Token입니다.",
              "result": null
            }
            """;

    public static final String SIGNUP_SUCCESS = """
            {
              "success": true,
              "code": "MEMBER201",
              "message": "회원가입에 성공했습니다.",
              "result": {
                "memberId": 1,
                "email": "user@example.com",
                "nickname": "backend"
              }
            }
            """;

    public static final String LOGIN_SUCCESS = """
            {
              "success": true,
              "code": "AUTH200",
              "message": "로그인에 성공했습니다.",
              "result": {
                "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                "tokenType": "Bearer"
              }
            }
            """;

    public static final String REISSUE_SUCCESS = """
            {
              "success": true,
              "code": "AUTH2001",
              "message": "토큰을 재발급했습니다.",
              "result": {
                "accessToken": "new-access-token",
                "refreshToken": "new-refresh-token",
                "tokenType": "Bearer"
              }
            }
            """;

    public static final String MEMBER_PROFILE_SUCCESS = """
            {
              "success": true,
              "code": "MEMBER200",
              "message": "내 정보를 조회했습니다.",
              "result": {
                "memberId": 1,
                "email": "user@example.com",
                "nickname": "backend",
                "role": "USER",
                "status": "ACTIVE"
              }
            }
            """;

    public static final String POST_NOT_FOUND = """
            {
              "success": false,
              "code": "POST404",
              "message": "게시글을 찾을 수 없습니다.",
              "result": null
            }
            """;

    public static final String COMMENT_NOT_FOUND = """
            {
              "success": false,
              "code": "COMMENT404",
              "message": "댓글을 찾을 수 없습니다.",
              "result": null
            }
            """;

    public static final String CATEGORY_NOT_FOUND = """
            {
              "success": false,
              "code": "CATEGORY404",
              "message": "카테고리를 찾을 수 없습니다.",
              "result": null
            }
            """;

    public static final String IMAGE_NOT_FOUND = """
            {
              "success": false,
              "code": "IMAGE404",
              "message": "이미지를 찾을 수 없습니다.",
              "result": null
            }
            """;

    public static final String IMAGE_COUNT_EXCEEDED = """
            {
              "success": false,
              "code": "IMAGE4003",
              "message": "게시글에 첨부할 수 있는 이미지 개수를 초과했습니다.",
              "result": null
            }
            """;

    public static final String IMAGE_TOO_LARGE = """
            {
              "success": false,
              "code": "IMAGE413",
              "message": "이미지 파일 크기가 허용 범위를 초과했습니다.",
              "result": null
            }
            """;

    public static final String IMAGE_UNSUPPORTED_TYPE = """
            {
              "success": false,
              "code": "IMAGE415",
              "message": "지원하지 않는 이미지 형식입니다.",
              "result": null
            }
            """;

    public static final String INTERNAL_SERVER_ERROR = """
            {
              "success": false,
              "code": "COMMON500",
              "message": "서버 내부 오류가 발생했습니다.",
              "result": null
            }
            """;

    private SwaggerExamples() {
    }
}