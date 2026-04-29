# Testing And Verification

## Test strategy

- 이 프로젝트의 기본 테스트 전략: 단위 테스트 중심. Service 계층 로직 검증 우선
- 단위 / 통합 / E2E 우선순위:
  1. 단위 테스트 (Service, Mapper, Util)
  2. 슬라이스 테스트 (Controller: `@WebMvcTest`, Repository: `@DataJpaTest`)
  3. 통합 테스트 (최소화)
- mock 사용 기준:
  - Service 단위 테스트: Mockito로 Repository, AdminNotifier mock
  - JPA Repository 테스트: H2 또는 Testcontainers PostgreSQL 사용, mock 금지
  - Controller 테스트: `MockMvc` + Service mock
- 회귀 테스트 기준: ErrorCode 추가, Entity 변경, Service 로직 변경 시 관련 테스트 업데이트

## Commands

- 최소 검증 명령: `./gradlew compileJava`
- 구현 후 반드시 실행해야 하는 명령: `./gradlew test`
- PR 전 반드시 실행해야 하는 명령: `./gradlew clean build` (GitHub Actions에서 `-x test`로 빌드)
- 일부 모듈만 빠르게 검증: `./gradlew test --tests "com.chuseok22.timemateserver.meeting.*"`

## Test conventions

테스트 파일 위치: `src/test/java/com/chuseok22/timemateserver/` (main과 동일한 패키지 구조)

```java
// Service 단위 테스트 패턴
@ExtendWith(MockitoExtension.class)
class MeetingRoomServiceTest {

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    @InjectMocks
    private MeetingRoomServiceImpl meetingRoomService;

    @Test
    @DisplayName("미팅룸 생성 성공")
    void createRoom_success() { ... }

    @Test
    @DisplayName("joinCode 중복 시 CustomException 발생")
    void createRoom_joinCodeDuplicate_throwsCustomException() { ... }
}
```

- 메서드명: `methodName_scenario_expectedBehavior()` 패턴
- `@DisplayName`: 한국어 설명 작성
- 테스트 내 `//given //when //then` 주석으로 구분

## Evidence

- 테스트 성공/실패를 어떻게 기록할지: `./gradlew test` 결과 요약 (`BUILD SUCCESSFUL / FAILED`)
- 스크린샷 / 로그 / 요약 결과 작성 방식: 터미널 출력 텍스트로 report에 포함
- UI 작업 시 필요한 검증 산출물: 해당 없음 (백엔드 서버)

## Failure handling

- 실패 시 우선 확인할 것:
  1. `build/reports/tests/test/index.html` 에서 상세 실패 원인 확인
  2. `@Transactional` 처리 여부 (테스트 격리)
  3. 스프링 컨텍스트 로드 실패 시 설정 파일 확인 (application-dev.yml 필요)
- flaky 판단 기준: 동일 조건에서 3회 이상 간헐적 실패 시 flaky로 분류
- 실패 결과 분석 시 필요한 로그 위치: `build/reports/tests/test/`
