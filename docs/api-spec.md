# 게시판 API 명세 초안

API 기본 경로:

```text
/api/v1
```

# 1. 인증 API

| Method | URL             | 인증 | 설명               |
| ------ | --------------- | -: | ---------------- |
| POST   | `/auth/signup`  |  X | 회원가입             |
| POST   | `/auth/login`   |  X | 로그인              |
| POST   | `/auth/reissue` |  X | Access Token 재발급 |
| POST   | `/auth/logout`  |  O | 로그아웃             |

## 회원가입

```http
POST /api/v1/auth/signup
```

```json
{
  "email": "user@example.com",
  "password": "Password123!",
  "nickname": "backend"
}
```

성공:

```json
{
  "success": true,
  "code": "MEMBER201",
  "message": "회원가입에 성공했습니다.",
  "result": {
    "memberId": 1
  }
}
```

## 로그인

```http
POST /api/v1/auth/login
```

```json
{
  "email": "user@example.com",
  "password": "Password123!"
}
```

성공:

```json
{
  "success": true,
  "code": "AUTH200",
  "message": "로그인에 성공했습니다.",
  "result": {
    "accessToken": "access-token",
    "refreshToken": "refresh-token",
    "tokenType": "Bearer"
  }
}
```

# 2. 회원 API

| Method | URL           | 인증 | 설명      |
| ------ | ------------- | -: | ------- |
| GET    | `/members/me` |  O | 내 정보 조회 |

# 3. 카테고리 API

| Method | URL                              |    인증 | 설명         |
| ------ | -------------------------------- | ----: | ---------- |
| GET    | `/categories`                    |     X | 활성 카테고리 목록 |
| POST   | `/admin/categories`              | ADMIN | 카테고리 생성    |
| PATCH  | `/admin/categories/{categoryId}` | ADMIN | 카테고리 수정    |

# 4. 게시글 API

| Method | URL               | 인증 | 설명        |
| ------ | ----------------- | -: | --------- |
| POST   | `/posts`          |  O | 게시글 작성    |
| GET    | `/posts`          |  X | 게시글 목록 조회 |
| GET    | `/posts/{postId}` | 선택 | 게시글 상세 조회 |
| PATCH  | `/posts/{postId}` |  O | 게시글 수정    |
| DELETE | `/posts/{postId}` |  O | 게시글 삭제    |

## 게시글 작성

```http
POST /api/v1/posts
Authorization: Bearer {accessToken}
```

```json
{
  "categoryId": 1,
  "title": "Spring Data JPA 정리",
  "content": "오늘 공부한 내용을 정리합니다.",
  "imageIds": [1, 2]
}
```

## 게시글 목록 조회

```http
GET /api/v1/posts?page=0&size=20&keyword=spring&categoryId=1&sort=latest
```

Query Parameter:

| 이름         | 필수 | 기본값    | 설명                   |
| ---------- | -: | ------ | -------------------- |
| page       |  X | 0      | 페이지 번호               |
| size       |  X | 20     | 페이지 크기               |
| keyword    |  X | 없음     | 제목 또는 내용 검색어         |
| author     |  X | 없음     | 작성자 닉네임              |
| categoryId |  X | 없음     | 카테고리                 |
| sort       |  X | latest | latest, likes, views |

응답:

```json
{
  "success": true,
  "code": "POST200",
  "message": "게시글 목록을 조회했습니다.",
  "result": {
    "posts": [
      {
        "postId": 1,
        "title": "Spring Data JPA 정리",
        "authorNickname": "backend",
        "categoryName": "공부 기록",
        "thumbnailUrl": "https://example.com/image.png",
        "commentCount": 3,
        "likeCount": 10,
        "viewCount": 25,
        "createdAt": "2026-06-30T13:00:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "hasNext": false
  }
}
```

## 게시글 상세 조회

```http
GET /api/v1/posts/{postId}
```

응답:

```json
{
  "success": true,
  "code": "POST200",
  "message": "게시글을 조회했습니다.",
  "result": {
    "postId": 1,
    "title": "Spring Data JPA 정리",
    "content": "오늘 공부한 내용을 정리합니다.",
    "author": {
      "memberId": 1,
      "nickname": "backend"
    },
    "category": {
      "categoryId": 1,
      "name": "공부 기록"
    },
    "images": [
      {
        "imageId": 1,
        "imageUrl": "https://example.com/image.png",
        "sortOrder": 0
      }
    ],
    "commentCount": 3,
    "likeCount": 10,
    "viewCount": 25,
    "likedByMe": false,
    "createdAt": "2026-06-30T13:00:00",
    "updatedAt": "2026-06-30T13:00:00"
  }
}
```

# 5. 댓글 API

| Method | URL                        | 인증 | 설명       |
| ------ | -------------------------- | -: | -------- |
| POST   | `/posts/{postId}/comments` |  O | 댓글 작성    |
| GET    | `/posts/{postId}/comments` |  X | 댓글 목록 조회 |
| PATCH  | `/comments/{commentId}`    |  O | 댓글 수정    |
| DELETE | `/comments/{commentId}`    |  O | 댓글 삭제    |

댓글 작성 요청:

```json
{
  "content": "좋은 글 감사합니다."
}
```

# 6. 좋아요 API

| Method | URL                     | 인증 | 설명     |
| ------ | ----------------------- | -: | ------ |
| PUT    | `/posts/{postId}/likes` |  O | 좋아요 등록 |
| DELETE | `/posts/{postId}/likes` |  O | 좋아요 취소 |

좋아요 등록과 취소 API는 반복 호출해도 최종 상태가 동일하도록 구현한다.

# 7. 이미지 API

| Method | URL                 | 인증 | 설명      |
| ------ | ------------------- | -: | ------- |
| POST   | `/images`           |  O | 이미지 업로드 |
| DELETE | `/images/{imageId}` |  O | 이미지 삭제  |

이미지 업로드:

```http
POST /api/v1/images
Content-Type: multipart/form-data
Authorization: Bearer {accessToken}
```

응답:

```json
{
  "success": true,
  "code": "IMAGE201",
  "message": "이미지를 업로드했습니다.",
  "result": {
    "imageId": 1,
    "imageUrl": "https://example.com/image.png"
  }
}
```

# 8. 주요 오류 코드

| 코드          | HTTP 상태 | 설명            |
| ----------- | ------: | ------------- |
| COMMON400   |     400 | 잘못된 요청        |
| COMMON401   |     401 | 인증이 필요함       |
| COMMON403   |     403 | 접근 권한 없음      |
| MEMBER404   |     404 | 회원을 찾을 수 없음   |
| MEMBER4091  |     409 | 이메일 중복        |
| MEMBER4092  |     409 | 닉네임 중복        |
| AUTH4011    |     401 | 로그인 정보 불일치    |
| AUTH4012    |     401 | 유효하지 않은 토큰    |
| POST404     |     404 | 게시글을 찾을 수 없음  |
| COMMENT404  |     404 | 댓글을 찾을 수 없음   |
| CATEGORY404 |     404 | 카테고리를 찾을 수 없음 |
| LIKE409     |     409 | 이미 좋아요가 등록됨   |
| IMAGE400    |     400 | 지원하지 않는 이미지   |
