# meet-time-server

## 프로젝트 개요

미팅 시간 조율 서비스의 백엔드 API 서버. 참가자들이 미팅룸을 생성하고, 날짜별 가용 시간을 등록하여 최적 미팅 시간을 찾을 수 있다.

- API 서버: https://api.meet.chuseok22.com
- Swagger UI: https://api.meet.chuseok22.com/docs/swagger

## 기술 스택

| 항목 | 내용 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.6 |
| Build | Gradle 8.14.3 |
| Database | PostgreSQL |
| ORM | Spring Data JPA (Hibernate) |
| Deployment | Docker + Blue/Green 무중단배포 |

## 주요 명령어

```bash
# 빌드 (테스트 제외)
./gradlew clean build -x test

# 테스트 실행
./gradlew test

# 로컬 실행 (dev 프로파일)
./gradlew bootRun --args='--spring.profiles.active=dev'

# 컴파일만 확인
./gradlew compileJava
```

## 소스 구조

```
src/main/java/com/chuseok22/timemateserver/
├── TimeMateServerApplication.java
├── common/                        # 공통 모듈
│   ├── core/exception/            # CustomException, ErrorCode, GlobalExceptionHandler
│   ├── application/aop/           # AOP 어노테이션 및 Aspect
│   └── infrastructure/
│       ├── config/                # CorsConfig, SwaggerConfig, OkHttpConfig
│       ├── persistence/           # BasePostgresEntity
│       └── properties/            # SpringDocProperties
├── admin/                         # 관리자 모듈 (Telegram 알림)
│   ├── core/service/              # AdminNotifier 인터페이스
│   ├── application/service/       # TelegramNotifier 구현체
│   └── infrastructure/            # TelegramConfig, TelegramProperties
└── meeting/                       # 핵심 도메인 모듈
    ├── core/
    │   ├── service/               # Service 인터페이스
    │   ├── repository/            # Repository 인터페이스
    │   └── constant/              # TimeSlot, JoinCodeAlphabet
    ├── application/
    │   ├── controller/            # REST Controller
    │   ├── service/               # Service 구현체 (*Impl)
    │   ├── repository/            # Repository 구현체 (*Impl)
    │   ├── dto/request|response/  # 요청/응답 DTO
    │   └── mapper/                # DTO ↔ Entity 변환
    └── infrastructure/
        ├── entity/                # JPA Entity
        ├── repository/            # JPA Repository 인터페이스
        ├── config/                # JoinCodeConfig
        ├── properties/            # JoinCodeProperties
        └── util/                  # JoinCodeUtil
```

## 아키텍처 규칙

- **core** → 도메인 인터페이스 및 상수. 프레임워크 의존성 없음
- **application** → 비즈니스 로직 구현. core 인터페이스를 구현하고 infrastructure를 사용
- **infrastructure** → JPA Entity, JPA Repository, 외부 연동 설정
- 의존 방향: `controller → service interface (core) → repository interface (core)` / 구현체는 application 계층

## 환경 설정

- 기본 프로파일: `prod`
- 로컬 개발: `dev` (src/main/resources/application-dev.yml)
- `application-prod.yml`은 GitHub Secret에서 주입되며 로컬에는 존재하지 않음
- `.env` 파일 및 비밀 정보는 절대 수정하지 않음

## 프로젝트별 규칙

- `.claude/rules/` 하위 파일이 상세 규칙의 기준
- 글로벌 CLAUDE.md와 충돌 시 이 파일의 규칙이 우선
- `application-dev.yml`에 로컬 전용 시크릿이 포함되어 있으므로 내용 노출 금지
