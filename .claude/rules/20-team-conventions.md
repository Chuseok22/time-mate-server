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

## Swagger ControllerDocs 작성 규칙

모든 컨트롤러는 반드시 대응하는 ControllerDocs 인터페이스를 가져야 한다.

### 위치

```
{module}/application/controller/docs/{Controller명}Docs.java
```

예시: `MeetingRoomController` → `meeting/application/controller/docs/MeetingRoomControllerDocs.java`

### 구조

```java
@Tag(name = "태그명", description = "그룹 설명")
public interface MeetingRoomControllerDocs {

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "YYYY-MM-DD",
          author = "Chuseok22",
          description = "변경 내용 요약",
          issueUrl = "https://github.com/Chuseok22/meet-time-server/issues/번호"
      )
  })
  @Operation(
      summary = "API 한줄 요약",
      description = """
          ### 요청 파라미터
          - `필드명` (타입, 필수여부): 설명

          ### 응답 데이터
          - `필드명` (타입): 설명

          ### 유의 사항
          - 주요 에러 케이스 및 제약 조건 기술
          """
  )
  ResponseEntity<응답타입> 메서드명(파라미터);
}
```

### 필수 import

```java
import com.chuseok22.apichangelog.annotation.ApiChangeLog;
import com.chuseok22.apichangelog.annotation.ApiChangeLogs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
```

### 컨트롤러 구현

```java
public class MeetingRoomController implements MeetingRoomControllerDocs {
  // Docs 인터페이스의 어노테이션은 컨트롤러 메서드에 중복 작성하지 않음
}
```

### @ApiChangeLog 작성 기준

- 신규 API 추가 시: 해당 구현 날짜와 이슈 URL 기재
- API 스펙 변경 시 (파라미터 추가/제거, 응답 구조 변경): 변경 이력 항목 추가 (최신순 정렬)
- 단순 리팩토링이나 내부 구현 변경은 이력 추가 대상이 아님

### @Operation description 작성 기준

- 요청 파라미터: 타입, 필수 여부, 허용 값 범위 명시
- 응답 데이터: 중첩 구조는 들여쓰기로 표현
- 유의 사항: 에러 케이스(404, 400 등), 비즈니스 제약, 데이터 처리 방식(Upsert 여부 등) 기술
- 경로 파라미터는 `@Parameter(description = "설명", required = true)` 추가

### 리뷰 체크 항목 (추가)

- [ ] 새 컨트롤러 생성 시 ControllerDocs 인터페이스가 함께 생성되었는지
- [ ] 새 API 엔드포인트 추가 시 ControllerDocs에 메서드 추가 및 @ApiChangeLog 이력 기재 여부
- [ ] API 스펙 변경 시 @ApiChangeLog 이력 업데이트 여부

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
