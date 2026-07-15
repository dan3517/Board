# Board — Spring Boot 게시판 REST API 서버

Spring Boot 백엔드 스터디의 최종 개인 프로젝트입니다.
회원가입과 로그인을 거쳐 게시글·댓글을 작성하고 좋아요를 누를 수 있는 REST API 기반 게시판 서버를, 요구사항 정의부터 DB 설계, 구현, 배포 자동화까지 진행했습니다.

AI를 적극 활용해 구현하고, 작성된 코드를 역으로 분석하며 동작 구조를 학습하는 방식으로 진행한 프로젝트입니다.

## 기술 스택

- **Framework**: Spring Boot, Spring Data JPA, Spring Security
- **인증**: JWT (Access Token + Refresh Token)
- **조회**: QueryDSL (검색·필터·정렬), 페이징
- **스토리지**: AWS S3 (게시글 이미지 업로드)
- **배포**: GitHub Actions + AWS (CI/CD)
- **문서화**: Swagger

## 주요 기능

- **회원**: 회원가입, 로그인, 토큰 재발급, 로그아웃, 내 정보 조회
- **게시글**: CRUD, 페이징 목록 조회, 제목·내용·작성자 검색, 카테고리 필터, 최신순·좋아요순·조회순 정렬
- **댓글**: 게시글별 댓글 CRUD
- **좋아요**: 게시글 좋아요 등록·취소 (사용자당 1회)
- **이미지**: S3 업로드 (게시글당 최대 5개, 파일 크기·형식 검증)
- **공통**: 통일된 API 응답 형식, 전역 예외 처리, 논리 삭제 정책

## 문서

| 문서 | 내용 |
|---|---|
| [요구사항 정의](docs/requirements.md) | 기능별 상세 요구사항 및 공통 정책 |
| [ERD](docs/erd.md) | 데이터베이스 설계 |
| [API 명세](docs/api-spec.md) | REST API 상세 명세 |

## 배포

`main` 브랜치에 push 시 GitHub Actions 워크플로([ci-cd.yml](.github/workflows/ci-cd.yml))를 통해 AWS에 자동 배포됩니다.
