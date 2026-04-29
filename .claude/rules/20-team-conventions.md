# Team Conventions

## Naming

- 변수명 규칙: camelCase (`meetingRoom`, `joinCode`)
- 메서드명 규칙: camelCase 동사 시작 (`createRoom`, `getRoomInfo`, `findByJoinCode`)
- 클래스 / 컴포넌트명 규칙: PascalCase
  - Service 인터페이스: `MeetingRoomService`
  - Service 구현체: `MeetingRoomServiceImpl`
  - Repository 인터페이스(core): `MeetingRoomRepository`
  - Repository 구현체(application): `MeetingRoomRepositoryImpl`
  - JPA Repository(infrastructure): `MeetingRoomJpaRepository`
  - Controller: `MeetingRoomController`
  - Entity: `MeetingRoom`
  - DTO: `CreateRoomRequest`, `RoomInfoResponse`
  - Mapper: `MeetingRoomMapper`
  - Properties: `JoinCodeProperties`
- 축약 금지: `mgr`, `svc` 등 불분명한 축약어 사용 금지

## Code style

- 명시적 타입 사용 규칙: 지역 변수에 `var` 사용 가능하지만 타입이 명확할 때만
- any 사용 여부: 해당 없음 (Java)
- deprecated API 사용 기준: 사용 금지. 현재 `ant_path_matcher`는 레거시이나 기존 설정 유지
- 에러 처리 방식:
  - 모든 도메인 예외는 `ErrorCode` enum에 정의
  - `throw new CustomException(ErrorCode.XXX)` 패턴으로 던지기
  - `GlobalExceptionHandler`에서 일괄 처리 (`@RestControllerAdvice`)
  - 예외 메시지에 민감 정보 포함 금지
- 주석 작성 기준:
  - 한국어로 작성
  - 비즈니스 의도, 도메인 제약, 비자명한 동작에만 추가
  - 코드를 그대로 설명하는 주석 금지

## Lombok 사용 패턴

```java
@Entity
@Getter                          // getter만 허용, setter 금지
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA 요구사항
public class MeetingRoom extends BasePostgresEntity { ... }

@Service
@Slf4j
@RequiredArgsConstructor         // 생성자 주입 (필드 주입 금지)
public class MeetingRoomServiceImpl implements MeetingRoomService { ... }
```

- `@Setter` 사용 금지 — Entity 상태 변경은 도메인 메서드로
- `@Data` 사용 금지 — `@Getter` + `@Builder` 조합 사용
- `log.warn()`, `log.error()` 시 구체적인 컨텍스트 포함

## Responsibility separation

- **Controller**: HTTP 요청/응답 처리, 입력값 검증(@Valid), Service 인터페이스 호출만
- **Service 인터페이스(core)**: 비즈니스 계약 정의
- **Service 구현체(application)**: 비즈니스 로직, 트랜잭션 관리, Repository + Mapper 조합
- **Repository 인터페이스(core)**: 데이터 접근 계약 정의 (프레임워크 독립)
- **Repository 구현체(application)**: JPA Repository 래핑, 예외 변환 (미발견 시 `CustomException` throw)
- **JPA Repository(infrastructure)**: `JpaRepository<Entity, ID>` 확장, DB 쿼리만
- **Mapper**: DTO ↔ Entity 변환 로직만. `@Component` 빈으로 등록
- **Entity**: DB 상태 관리. 정적 팩토리 메서드(`create()`) 허용, 비즈니스 로직 최소화

## AOP 어노테이션 사용 기준

| 어노테이션 | 적용 위치 | 용도 |
|-----------|-----------|------|
| `@LogMonitoringInvocation` | Controller 메서드 | 요청 모니터링 로깅 |
| `@LogTimeInvocation` | Service/Repository 메서드 | 실행 시간 측정 |
| `@LogMethodInvocation` | 일반 메서드 | 메서드 호출 로깅 |

## Review expectations

- 리뷰 시 반드시 확인할 항목:
  - 계층 의존 방향 준수 여부
  - Repository 구현체가 예외를 `CustomException`으로 올바르게 변환하는지
  - Entity에 Setter 메서드가 추가되지 않았는지
  - 필드 주입(`@Autowired`) 없이 생성자 주입 사용 여부
  - 새 에러 케이스에 `ErrorCode` 추가 여부
- 성능 / 보안 / 유지보수 관점 체크리스트:
  - `@Transactional(readOnly = true)` 조회 메서드에 적용 여부
  - N+1 쿼리 발생 가능성 확인
  - 시크릿 정보 로그 출력 여부
- 리뷰에서 block 걸어야 하는 기준:
  - core 계층이 application/infrastructure를 참조
  - Entity를 Controller 응답으로 직접 반환
  - 하드코딩된 시크릿 정보 포함
