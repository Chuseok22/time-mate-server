# Project Overview

## Purpose

- 이 프로젝트의 목적: 미팅 시간 조율 서비스 백엔드 API 서버
- 해결하려는 문제: 여러 참가자의 가용 시간을 취합하여 최적 미팅 시간 도출
- 주요 사용자 또는 시스템 소비자: meet-time-web 프론트엔드, REST API 클라이언트

## Primary Stack

- Language: Java 21
- Framework: Spring Boot 3.5.6
- Runtime: JRE (eclipse-temurin:21-jre-alpine)
- Database / storage: PostgreSQL (Spring Data JPA / Hibernate)
- Infra / deployment: Docker + GitHub Actions + Blue/Green 무중단배포 (Synology NAS)

## Important directories

- src/main/java/com/chuseok22/timemateserver/: 전체 소스 루트
- meeting/: 핵심 도메인 (MeetingRoom, MeetingDate, Participant, AvailabilityTime)
- admin/: 관리자 기능 (Telegram 알림)
- common/: 공통 인프라 (예외처리, AOP, 설정)
- src/main/resources/: application.yml, application-dev.yml, application-prod.yml(로컬 없음)
- src/test/: 테스트 코드 (main과 동일한 패키지 구조 유지)
- .github/workflows/: CI/CD 파이프라인 (time-mate-build.yml, time-mate-cicd.yml)

## Main commands

- install: `./gradlew dependencies`
- lint: 없음 (현재 Checkstyle 미적용)
- typecheck: `./gradlew compileJava`
- build: `./gradlew clean build -x test`
- unit-test: `./gradlew test`
- integration-test: 별도 없음 (현재 미구성)
- e2e-test: 별도 없음
- run-dev: `./gradlew bootRun --args='--spring.profiles.active=dev'`
- run-prod-like: `./gradlew clean build && java -jar build/libs/*.jar --spring.profiles.active=prod`

## Project-specific constraints

- 반드시 지켜야 하는 제약:
  - `application-prod.yml`은 GitHub Secret으로만 관리. 로컬에 생성하거나 커밋 금지
  - `application-dev.yml`에 시크릿 포함됨. 내용 외부 노출 금지
  - 모든 비밀정보(토큰, 비밀번호, API Key)는 코드에 하드코딩 금지
- 사용 금지 기술 / 패턴:
  - 필드 주입(@Autowired 필드) 금지 → 생성자 주입(@RequiredArgsConstructor) 사용
  - Setter 메서드로 Entity 상태 변경 금지 → 정적 팩토리 메서드 또는 도메인 메서드 사용
- 현재 프로젝트에서 중요하게 보는 품질 기준:
  - 계층 의존 방향 준수 (core ← application ← infrastructure)
  - 에러는 반드시 ErrorCode enum 통해 CustomException으로 처리

## Change policy

- 어떤 변경은 허용되고 어떤 변경은 금지되는지:
  - meeting/, admin/, common/ 하위 비즈니스 로직 수정 허용
  - .github/workflows/ 변경은 배포 영향 → 신중하게
- 지금 레포에서 수정해도 되는 범위: src/ 전체
- 절대 건드리면 안 되는 영역: application-prod.yml, .env 류 파일, GitHub Secret 값
