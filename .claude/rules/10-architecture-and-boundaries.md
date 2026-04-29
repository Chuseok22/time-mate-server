# Architecture And Boundaries

## High-level architecture

- 현재 시스템의 상위 구조: 단일 Spring Boot 애플리케이션 (모놀리식)
- 주요 계층:
  1. **core** — 도메인 인터페이스 계층. Service 인터페이스, Repository 인터페이스, 도메인 상수. 프레임워크 의존성 없음
  2. **application** — 애플리케이션 계층. Controller, Service 구현체, Repository 구현체, DTO, Mapper
  3. **infrastructure** — 인프라 계층. JPA Entity, JPA Repository, 외부 연동 설정, Properties
- 외부 시스템 연동:
  - PostgreSQL (JPA)
  - Telegram Bot API (OkHttp 사용, 방 생성 알림)
  - Spring Actuator (헬스체크 `/actuator/health`)
  - Swagger UI (`/docs/swagger`)

## Module boundaries

각 모듈의 책임:

| 모듈 | 책임 |
|------|------|
| `meeting` | 핵심 도메인. MeetingRoom, MeetingDate, Participant, AvailabilityTime 관리 |
| `admin` | 관리자 알림. Telegram을 통한 이벤트 알림 발송 |
| `common` | 공통 인프라. 예외처리, AOP 로깅, CORS/Swagger 설정, BaseEntity |

모듈 간 허용 의존 방향:
- `meeting.application.service` → `admin.core.service.AdminNotifier` (인터페이스만)
- `meeting`, `admin`, `common` 모두 → `common.core.exception`

금지 의존 관계:
- `core` 계층이 `application` 또는 `infrastructure` 계층을 참조하는 것 금지
- `infrastructure.entity`를 Controller의 반환값으로 직접 사용 금지 → DTO 변환 필수

## Data flow

요청/응답 흐름:
```
Client → Controller (@RestController)
       → Service Interface (core)
       → Service Impl (application) → Repository Interface (core)
                                    → Repository Impl (application) → JPA Repository (infrastructure)
                                                                     → Entity (infrastructure)
       → Mapper → Response DTO → Controller → ResponseEntity
```

비동기 처리 흐름:
- 현재 비동기 처리 없음. 모든 흐름 동기

캐시/큐/스토리지 사용 방식:
- 현재 캐시 없음
- 파일 스토리지: Docker 볼륨 마운트(`/volume1/project/meet-time/server`)

## File / folder conventions

폴더 구조 규칙:
```
{module}/
├── core/
│   ├── service/          # XXXService.java (인터페이스)
│   ├── repository/       # XXXRepository.java (인터페이스)
│   └── constant/         # 도메인 enum, 상수
├── application/
│   ├── controller/       # XXXController.java
│   ├── service/          # XXXServiceImpl.java
│   ├── repository/       # XXXRepositoryImpl.java
│   ├── dto/
│   │   ├── request/      # XXXRequest.java
│   │   └── response/     # XXXResponse.java
│   └── mapper/           # XXXMapper.java
└── infrastructure/
    ├── entity/           # XXX.java (JPA Entity)
    ├── repository/       # XXXJpaRepository.java (extends JpaRepository)
    ├── config/           # XXXConfig.java (@Configuration)
    ├── properties/       # XXXProperties.java (@ConfigurationProperties)
    └── util/             # XXXUtil.java
```

새 파일 생성 시 위치 기준:
- 도메인 인터페이스 → `{module}/core/`
- 비즈니스 로직 → `{module}/application/service/`
- JPA Entity → `{module}/infrastructure/entity/`
- @Configuration 클래스 → `infrastructure/config/`
- @ConfigurationProperties → `infrastructure/properties/`

공통 유틸 / 도메인 로직 / API 계층 분리 기준:
- 여러 모듈에서 쓰는 유틸 → `common/core/util/` 또는 `common/application/`
- 특정 도메인 유틸 → 해당 도메인의 `infrastructure/util/`

## Extension points

기능 추가 시 먼저 참고해야 하는 패턴:
- 새 API 엔드포인트: `MeetingRoomController` + `MeetingRoomService` + `MeetingRoomServiceImpl` 패턴 참고
- 새 도메인 에러: `ErrorCode` enum에 추가 → `CustomException`으로 throw
- 새 외부 알림: `AdminNotifier` 인터페이스 확장 패턴 참고

기존 구현 재사용 포인트:
- Entity: `BasePostgresEntity` 상속 (createdAt, updatedAt 자동 관리)
- 예외: `CustomException(ErrorCode.XXX)` 패턴 재사용
- AOP: `@LogMonitoringInvocation` (Controller 메서드 로깅), `@LogTimeInvocation` (실행시간 측정)

대표적으로 따라야 하는 파일 경로 예시:
- `meeting/core/service/MeetingRoomService.java` — Service 인터페이스
- `meeting/application/service/MeetingRoomServiceImpl.java` — Service 구현체
- `meeting/application/repository/MeetingRoomRepositoryImpl.java` — Repository 구현체
- `meeting/infrastructure/entity/MeetingRoom.java` — JPA Entity
- `meeting/infrastructure/repository/MeetingRoomJpaRepository.java` — JPA Repository
