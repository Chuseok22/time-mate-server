# Meet Time — 프론트엔드 API 연동 가이드

> 서비스 기획 및 전체 목표는 [service-goals.md](./service-goals.md) 참고.
> Swagger UI: **https://api.meet.chuseok22.com/docs/swagger**

---

## 목차

1. [서버 정보](#1-서버-정보)
2. [인증 방식](#2-인증-방식)
3. [소셜 로그인 흐름](#3-소셜-로그인-흐름)
4. [공통 에러 응답](#4-공통-에러-응답)
5. [미팅룸 API](#5-미팅룸-api)
6. [참가자 API](#6-참가자-api)
7. [가용 시간 API](#7-가용-시간-api)
8. [사용자 API](#8-사용자-api)
9. [TimeSlot 참조표](#9-timeslot-참조표)
10. [주요 사용 시나리오](#10-주요-사용-시나리오)

---

## 1. 서버 정보

| 환경 | Base URL |
|------|----------|
| 운영 | `https://api.meet.chuseok22.com` |
| 테스트 | `http://chuseok22.synology.me:8092` |
| 로컬 | `http://localhost:8080` |

모든 요청/응답의 Content-Type은 `application/json;charset=UTF-8`.

---

## 2. 인증 방식

### Guest (비로그인 사용자)

- 인증 헤더 없이 공개 API 호출 가능.
- 방 참가 시 username과 password를 직접 입력.

### 로그인 사용자

소셜 로그인 완료 후 발급된 JWT를 모든 인증 필요 요청 헤더에 포함한다.

```
Authorization: Bearer <accessToken>
```

### 인증 필요 API 목록

아래 엔드포인트는 **JWT 없이 호출 시 401** 반환.

| 메서드 | 경로 |
|--------|------|
| `GET` | `/api/users/me` |
| `GET` | `/api/users/me/rooms` |
| `DELETE` | `/api/rooms/{room-id}` |
| `DELETE` | `/api/participant/{participant-id}` |

---

## 3. 소셜 로그인 흐름

### 지원 제공자

| 제공자 | 로그인 시작 URL |
|--------|----------------|
| Google | `https://api.meet.chuseok22.com/oauth2/authorization/google` |
| Kakao | `https://api.meet.chuseok22.com/oauth2/authorization/kakao` |

### 단계별 흐름

```
[1] 프론트에서 위 URL로 브라우저 이동 (window.location.href 또는 <a> 링크)
        ↓
[2] 서버 → 소셜 업체 로그인 페이지로 리다이렉트
        ↓
[3] 사용자가 소셜 계정으로 로그인
        ↓
[4] 소셜 업체 → 서버로 인증 코드 전달
        ↓
[5] 서버에서 사용자 정보 조회 후 DB 저장 (최초 로그인 시 자동 회원가입)
        ↓
[6] 서버 → 프론트 콜백 URL로 리다이렉트
        https://meet.chuseok22.com/callback?token=<JWT>
        ↓
[7] 프론트: URL에서 token 쿼리 파라미터 추출 후 저장
```

### 프론트 처리 코드 예시

```javascript
// /callback 페이지 진입 시
const params = new URLSearchParams(window.location.search);
const token = params.get('token');

if (token) {
  localStorage.setItem('accessToken', token);
  // 이후 API 호출 시 Authorization 헤더에 포함
} else {
  // 로그인 실패 또는 token 미포함 — 에러 처리
}
```

### 로그인 실패 시

소셜 인증 과정에서 오류 발생 시 아래 URL로 리다이렉트된다.

```
https://meet.chuseok22.com/login?error=oauth2_failed
```

프론트는 이 경로에서 에러 메시지를 노출하면 된다.

### JWT 유효 기간

JWT에 만료 시간이 포함되어 있다. 만료된 토큰으로 요청 시 `401 TOKEN_EXPIRED` 반환.  
만료 시 저장된 토큰을 삭제하고 로그인 페이지로 이동 처리 권장.

> **현재 서버는 Refresh Token을 발급하지 않는다.** 토큰 만료 시 소셜 로그인 재시도 필요.

---

## 4. 공통 에러 응답

### 일반 에러 응답

```json
{
  "errorCode": "MEETING_ROOM_NOT_FOUND",
  "errorMessage": "Meeting Room을 찾을 수 없습니다."
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `errorCode` | String | 에러 식별자 (코드 분기 시 사용) |
| `errorMessage` | String | 사용자에게 노출 가능한 한국어 메시지 |

### 유효성 검사 실패 응답 (400)

요청 파라미터가 @Valid 검사를 통과하지 못한 경우.

```json
{
  "errorCode": "400 BAD_REQUEST",
  "errorMessage": "잘못된 요청입니다.",
  "validation": {
    "title": "공백일 수 없습니다",
    "dates": "크기가 1에서 2147483647 사이여야 합니다"
  }
}
```

`validation` 필드는 실패한 각 필드명과 사유를 담는다.

### 에러 코드 전체 목록

| errorCode | HTTP 상태 | 설명 | 권장 처리 |
|-----------|-----------|------|-----------|
| `INVALID_REQUEST` | 400 | 잘못된 요청 파라미터 | 입력값 재확인 안내 |
| `BASE_58_JOIN_CODE_PATTERN_MISMATCH` | 400 | 방 코드 형식 불일치 | 올바른 형식 안내 |
| `TOKEN_EXPIRED` | 401 | JWT 만료 | 저장 토큰 삭제 후 로그인 페이지 이동 |
| `INVALID_TOKEN` | 401 | 유효하지 않은 JWT | 저장 토큰 삭제 후 로그인 페이지 이동 |
| `INVALID_PASSWORD` | 401 | 비밀번호 불일치 | 비밀번호 오류 메시지 노출 |
| `ACCESS_DENIED` | 403 | 접근 권한 없음 | 권한 없음 안내 |
| `ROOM_DELETE_FORBIDDEN` | 403 | 방장이 아닌 사용자의 방 삭제 시도 | 권한 없음 안내 |
| `PARTICIPANT_DELETE_FORBIDDEN` | 403 | 본인이 아닌 참가자 탈퇴 시도 | 권한 없음 안내 |
| `MEETING_ROOM_NOT_FOUND` | 404 | 미팅룸 없음 | "존재하지 않는 방" 안내 |
| `PARTICIPANT_NOT_FOUND` | 404 | 참가자 없음 | "존재하지 않는 참가자" 안내 |
| `USER_NOT_FOUND` | 404 | 사용자 없음 | 로그인 재시도 안내 |
| `DUPLICATE_USERNAME` | 409 | 동일 방 내 이름 중복 | 다른 이름 입력 안내 |
| `INTERNAL_SERVER_ERROR` | 500 | 서버 오류 | "잠시 후 다시 시도해주세요" |

---

## 5. 미팅룸 API

### 5-1. 미팅룸 생성

```
POST /api/rooms
```

방 생성은 로그인 사용자와 Guest 모두 가능하다.  
**단, 로그인 사용자만 방 삭제(DELETE) 가능하다.** Guest가 만든 방은 삭제할 수 없다.

#### 요청 헤더 (선택)

| 헤더 | 설명 |
|------|------|
| `Authorization: Bearer <token>` | 있으면 방 생성자(creatorUserId)로 userId 저장. 없으면 Guest 방으로 생성 |

#### 요청 본문

```json
{
  "title": "팀 회의 일정 조율",
  "dates": ["2026-05-10", "2026-05-11", "2026-05-12"]
}
```

| 필드 | 타입 | 필수 | 제약 | 설명 |
|------|------|------|------|------|
| `title` | String | 필수 | 1자 이상 | 미팅룸 제목 |
| `dates` | List\<LocalDate\> | 필수 | 1개 이상, YYYY-MM-DD | 후보 날짜 목록 |

> **중복 날짜**: 동일한 날짜가 여러 개 포함되어도 서버에서 자동 중복 제거.

#### 응답 (200 OK)

```json
{
  "meetingRoomId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "팀 회의 일정 조율",
  "joinCode": "AB1-CD2",
  "dates": ["2026-05-10", "2026-05-11", "2026-05-12"],
  "participantsCount": 0,
  "participantInfoResponses": [],
  "dateAvailabilityResponses": [
    {
      "date": "2026-05-10",
      "timeSlotParticipantsResponses": []
    },
    {
      "date": "2026-05-11",
      "timeSlotParticipantsResponses": []
    },
    {
      "date": "2026-05-12",
      "timeSlotParticipantsResponses": []
    }
  ]
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `meetingRoomId` | UUID | 생성된 방 ID. 이후 API 호출 시 사용 |
| `title` | String | 방 제목 |
| `joinCode` | String | 초대용 6자리 코드 (예: `AB1-CD2`). 대소문자 구분 |
| `dates` | List\<String\> | 후보 날짜 목록 (YYYY-MM-DD) |
| `participantsCount` | int | 현재 참가자 수 (생성 직후 0) |
| `participantInfoResponses` | List | 참가자 목록 (생성 직후 빈 배열) |
| `dateAvailabilityResponses` | List | 날짜별 가용 시간 현황 (생성 직후 빈 배열) |

#### 에러 케이스

| 상황 | HTTP | errorCode |
|------|------|-----------|
| `title` 누락 또는 빈 문자열 | 400 | `INVALID_REQUEST` |
| `dates` 누락 또는 빈 배열 | 400 | `INVALID_REQUEST` |

---

### 5-2. 미팅룸 조회 (ID)

```
GET /api/rooms/{room-id}
```

방 ID로 미팅룸 전체 정보를 조회한다.

#### 경로 파라미터

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `room-id` | UUID | 조회할 미팅룸 ID |

#### 응답 (200 OK)

```json
{
  "meetingRoomId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "팀 회의 일정 조율",
  "joinCode": "AB1-CD2",
  "dates": ["2026-05-10", "2026-05-11"],
  "participantsCount": 2,
  "participantInfoResponses": [
    { "participantId": "aaa-...", "username": "김철수" },
    { "participantId": "bbb-...", "username": "이영희" }
  ],
  "dateAvailabilityResponses": [
    {
      "date": "2026-05-10",
      "timeSlotParticipantsResponses": [
        {
          "timeSlot": "SLOT_09_00",
          "participantInfoResponses": [
            { "participantId": "aaa-...", "username": "김철수" }
          ],
          "availabilityCount": 1
        },
        {
          "timeSlot": "SLOT_09_30",
          "participantInfoResponses": [
            { "participantId": "aaa-...", "username": "김철수" },
            { "participantId": "bbb-...", "username": "이영희" }
          ],
          "availabilityCount": 2
        }
      ]
    }
  ]
}
```

**`dateAvailabilityResponses` 구조 상세**

| 필드 | 타입 | 설명 |
|------|------|------|
| `date` | String | 날짜 (YYYY-MM-DD) |
| `timeSlotParticipantsResponses` | List | 해당 날짜에 가용 시간을 등록한 슬롯 목록. 아무도 등록하지 않은 슬롯은 포함되지 않음 |
| `timeSlot` | String | 시간대 코드 (예: `SLOT_09_00` = 09:00) |
| `participantInfoResponses` | List | 해당 시간대에 참가 가능한 참가자 목록 |
| `availabilityCount` | int | 해당 시간대 참가 가능 인원 수 |

#### 에러 케이스

| 상황 | HTTP | errorCode |
|------|------|-----------|
| 존재하지 않는 `room-id` | 404 | `MEETING_ROOM_NOT_FOUND` |

---

### 5-3. 미팅룸 조회 (방 코드)

```
GET /api/rooms/join-code/{join-code}
```

초대용 방 코드로 미팅룸을 조회한다. 응답 구조는 [5-2](#5-2-미팅룸-조회-id)와 동일.

#### 경로 파라미터

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `join-code` | String | 초대용 방 코드 (예: `AB1-CD2`). **대소문자 구분** |

#### 에러 케이스

| 상황 | HTTP | errorCode |
|------|------|-----------|
| 존재하지 않는 방 코드 | 404 | `MEETING_ROOM_NOT_FOUND` |
| 방 코드 형식 불일치 | 400 | `BASE_58_JOIN_CODE_PATTERN_MISMATCH` |

---

### 5-4. 방 삭제

```
DELETE /api/rooms/{room-id}
Authorization: Bearer <token>  ← 필수
```

방 전체를 삭제한다. **참가자 기록, 가용 시간 포함 전체 삭제. 복구 불가.**

#### 동작 조건

- 로그인 사용자만 호출 가능 (JWT 필수)
- **방을 생성한 사용자(방장)만 삭제 가능**
- Guest가 만든 방(`creatorUserId` 없음)은 삭제 불가

#### 경로 파라미터

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `room-id` | UUID | 삭제할 방 ID |

#### 응답

성공 시 `204 No Content` 또는 `200 OK` (본문 없음).

#### 에러 케이스

| 상황 | HTTP | errorCode |
|------|------|-----------|
| JWT 미포함 | 401 | `TOKEN_EXPIRED` 또는 `INVALID_TOKEN` |
| 방장이 아닌 사용자 | 403 | `ROOM_DELETE_FORBIDDEN` |
| Guest가 만든 방 | 403 | `ROOM_DELETE_FORBIDDEN` |
| 존재하지 않는 `room-id` | 404 | `MEETING_ROOM_NOT_FOUND` |

---

## 6. 참가자 API

### 6-1. 방 참가 (로그인 / Guest)

```
POST /api/participant
```

미팅룸에 참가자로 입장한다. **로그인 사용자와 Guest의 동작이 다르다.**

#### 요청 본문

```json
{
  "meetingRoomId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "김철수",
  "password": "1234"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `meetingRoomId` | UUID | 필수 | 입장할 방 ID |
| `username` | String | Guest 필수 / 로그인 선택 | 참가자 이름 |
| `password` | String | 선택 | 참가자 비밀번호 (Guest 전용) |

#### 동작 상세 — Guest (Authorization 헤더 없음)

1. `username` 입력 필수. 공백 불가.
2. 동일 방에 같은 `username`이 없으면 **신규 참가자 생성**.
3. 같은 `username`이 이미 있으면 **비밀번호 검증** 후 기존 참가자 정보 반환.
   - 비밀번호가 설정된 경우: `password` 일치 필수.
   - 비밀번호 미설정 경우: `password` 없이 재입장 가능.

#### 동작 상세 — 로그인 사용자 (Authorization 헤더 있음)

1. 동일 방에 같은 userId로 참가한 기록이 있으면 **기존 참가자 정보 반환** (재입장).
2. 신규 참가 시 `username`이 비어있으면 **소셜 닉네임을 자동으로 사용**.
3. `username`을 명시하면 해당 이름으로 참가.
4. `password`는 무시된다 (로그인 사용자는 비밀번호 인증 불필요).

#### 참가 방식 비교

| 항목 | Guest | 로그인 사용자 |
|------|-------|--------------|
| 인증 헤더 | 없음 | `Authorization: Bearer <token>` |
| username | 직접 입력 (필수) | 소셜 닉네임 자동 사용 (변경 가능) |
| password | 선택 (재입장 시 검증) | 무시됨 |
| 재입장 기준 | 동일 username | 동일 userId (서비스 계정) |
| 방 탈퇴 | 불가 | 가능 (DELETE /api/participant/{id}) |

#### 응답 (200 OK)

```json
{
  "participantId": "aaa-bbb-ccc-ddd-eee",
  "username": "김철수"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `participantId` | UUID | 참가자 ID. 가용 시간 설정 시 사용 |
| `username` | String | 방에 등록된 참가자 이름 |

#### 에러 케이스

| 상황 | HTTP | errorCode |
|------|------|-----------|
| `meetingRoomId` 누락 | 400 | `INVALID_REQUEST` |
| Guest가 `username` 누락 | 400 | `INVALID_REQUEST` |
| 존재하지 않는 방 | 404 | `MEETING_ROOM_NOT_FOUND` |
| 비밀번호 불일치 | 401 | `INVALID_PASSWORD` |
| 동일 방 내 이름 중복 (다른 사람이 이미 사용 중) | 409 | `DUPLICATE_USERNAME` |

---

### 6-2. 참가자 정보 조회

```
GET /api/participant/{participant-id}
```

참가자 ID로 참가자 정보를 조회한다.

#### 경로 파라미터

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `participant-id` | UUID | 조회할 참가자 ID |

#### 응답 (200 OK)

```json
{
  "participantId": "aaa-bbb-ccc-ddd-eee",
  "username": "김철수"
}
```

#### 에러 케이스

| 상황 | HTTP | errorCode |
|------|------|-----------|
| 존재하지 않는 `participant-id` | 404 | `PARTICIPANT_NOT_FOUND` |

---

### 6-3. 방 탈퇴

```
DELETE /api/participant/{participant-id}
Authorization: Bearer <token>  ← 필수
```

참가 기록을 삭제한다. **가용 시간 투표 기록 포함 삭제. 복구 불가.**

#### 동작 조건

- 로그인 사용자만 호출 가능 (JWT 필수)
- **본인의 참가 기록만 삭제 가능** (userId 일치 확인)
- Guest 참가자(userId 없음)는 탈퇴 불가 — 403 반환

#### 경로 파라미터

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `participant-id` | UUID | 탈퇴할 참가자 ID |

#### 응답

성공 시 `204 No Content` 또는 `200 OK` (본문 없음).

#### 에러 케이스

| 상황 | HTTP | errorCode |
|------|------|-----------|
| JWT 미포함 | 401 | `TOKEN_EXPIRED` 또는 `INVALID_TOKEN` |
| Guest 참가자 탈퇴 시도 | 403 | `PARTICIPANT_DELETE_FORBIDDEN` |
| 본인이 아닌 참가자 탈퇴 시도 | 403 | `PARTICIPANT_DELETE_FORBIDDEN` |
| 존재하지 않는 `participant-id` | 404 | `PARTICIPANT_NOT_FOUND` |

---

## 7. 가용 시간 API

### 7-1. 가용 시간 설정 (Upsert)

```
POST /api/time
```

참가자가 날짜별로 참석 가능한 시간대를 등록한다.  
**기존 데이터 전체 교체 방식 (Upsert)** — 이전에 등록한 모든 가용 시간이 새 데이터로 덮어쓰인다.

#### 요청 본문

```json
{
  "participantId": "aaa-bbb-ccc-ddd-eee",
  "availabilityTimeRequests": [
    {
      "date": "2026-05-10",
      "timeSlots": ["SLOT_09_00", "SLOT_09_30", "SLOT_10_00"]
    },
    {
      "date": "2026-05-11",
      "timeSlots": ["SLOT_14_00", "SLOT_14_30"]
    }
  ]
}
```

| 필드 | 타입 | 필수 | 제약 | 설명 |
|------|------|------|------|------|
| `participantId` | UUID | 필수 | — | 가용 시간을 설정할 참가자 ID |
| `availabilityTimeRequests` | List | 필수 | — | 날짜별 가용 시간 목록 |
| `date` | String | 필수 | YYYY-MM-DD | 날짜 |
| `timeSlots` | List\<String\> | 선택 | 0~32개 | 시간대 코드 목록. 빈 배열 허용 |

#### 동작 규칙

- 호출 시 해당 참가자의 **모든 기존 가용 시간 삭제 후** 새 데이터 저장.
- `availabilityTimeRequests`에 포함하지 않은 날짜의 가용 시간은 자동으로 삭제된다.
- 특정 날짜의 `timeSlots`를 빈 배열 `[]`로 보내면 해당 날짜 가용 시간만 초기화.
- **전체 초기화**: `availabilityTimeRequests`를 빈 배열 `[]`로 보내면 모든 날짜 삭제.

#### 예시: 특정 날짜만 초기화

```json
{
  "participantId": "aaa-bbb-ccc-ddd-eee",
  "availabilityTimeRequests": [
    {
      "date": "2026-05-10",
      "timeSlots": []
    }
  ]
}
```

위 요청 후 `2026-05-10`의 가용 시간은 비워지고, `2026-05-11` 등 다른 날짜도 포함되지 않았으므로 함께 삭제된다.

> **가용 시간 수정 시 전체 데이터를 다시 보내야 한다.** 특정 날짜만 부분 업데이트하는 것이 아니라 전체 교체임에 주의.

#### 응답

성공 시 `200 OK` (본문 없음).

#### 에러 케이스

| 상황 | HTTP | errorCode |
|------|------|-----------|
| `participantId` 누락 | 400 | `INVALID_REQUEST` |
| 존재하지 않는 `participantId` | 404 | `PARTICIPANT_NOT_FOUND` |
| `timeSlots` 32개 초과 | 400 | `INVALID_REQUEST` |

---

## 8. 사용자 API

로그인 사용자 전용. 모든 요청에 `Authorization: Bearer <token>` 헤더 필수.

### 8-1. 내 정보 조회

```
GET /api/users/me
Authorization: Bearer <token>
```

JWT에 담긴 userId 기반으로 소셜 계정 정보를 반환한다.

#### 응답 (200 OK)

```json
{
  "userId": "user-uuid-here",
  "nickname": "홍길동",
  "email": "hong@gmail.com"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `userId` | UUID | 서비스 사용자 ID |
| `nickname` | String | 소셜 계정 닉네임 |
| `email` | String | 소셜 계정 이메일 |

#### 에러 케이스

| 상황 | HTTP | errorCode |
|------|------|-----------|
| JWT 미포함 또는 만료 | 401 | `TOKEN_EXPIRED` / `INVALID_TOKEN` |

---

### 8-2. 내 방 목록 조회

```
GET /api/users/me/rooms
Authorization: Bearer <token>
```

내가 만든 방 + 내가 참가자로 참여한 방을 통합하여 반환한다 (중복 제거).

#### 응답 (200 OK)

```json
[
  {
    "roomId": "550e8400-...",
    "title": "팀 회의 일정 조율",
    "joinCode": "AB1-CD2",
    "isOwner": true
  },
  {
    "roomId": "661f9511-...",
    "title": "스터디 날짜 잡기",
    "joinCode": "XY3-ZW4",
    "isOwner": false
  }
]
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `roomId` | UUID | 방 ID |
| `title` | String | 방 제목 |
| `joinCode` | String | 초대 코드 |
| `isOwner` | boolean | 내가 방장인지 여부 |

#### 에러 케이스

| 상황 | HTTP | errorCode |
|------|------|-----------|
| JWT 미포함 또는 만료 | 401 | `TOKEN_EXPIRED` / `INVALID_TOKEN` |

---

## 9. TimeSlot 참조표

`timeSlots` 필드에 사용되는 값 목록. 30분 단위.

| 코드 | 시간 | 코드 | 시간 |
|------|------|------|------|
| `SLOT_08_00` | 08:00 | `SLOT_16_00` | 16:00 |
| `SLOT_08_30` | 08:30 | `SLOT_16_30` | 16:30 |
| `SLOT_09_00` | 09:00 | `SLOT_17_00` | 17:00 |
| `SLOT_09_30` | 09:30 | `SLOT_17_30` | 17:30 |
| `SLOT_10_00` | 10:00 | `SLOT_18_00` | 18:00 |
| `SLOT_10_30` | 10:30 | `SLOT_18_30` | 18:30 |
| `SLOT_11_00` | 11:00 | `SLOT_19_00` | 19:00 |
| `SLOT_11_30` | 11:30 | `SLOT_19_30` | 19:30 |
| `SLOT_12_00` | 12:00 | `SLOT_20_00` | 20:00 |
| `SLOT_12_30` | 12:30 | `SLOT_20_30` | 20:30 |
| `SLOT_13_00` | 13:00 | `SLOT_21_00` | 21:00 |
| `SLOT_13_30` | 13:30 | `SLOT_21_30` | 21:30 |
| `SLOT_14_00` | 14:00 | `SLOT_22_00` | 22:00 |
| `SLOT_14_30` | 14:30 | `SLOT_22_30` | 22:30 |
| `SLOT_15_00` | 15:00 | `SLOT_23_00` | 23:00 |
| `SLOT_15_30` | 15:30 | `SLOT_23_30` | 23:30 |

범위: 08:00 ~ 23:30 (총 32개)

---

## 10. 주요 사용 시나리오

### 시나리오 A — Guest 방 생성 및 참가

```
1. POST /api/rooms
   → meetingRoomId, joinCode 저장

2. POST /api/participant  (헤더 없음)
   body: { meetingRoomId, username: "김철수", password: "1234" }
   → participantId 저장

3. POST /api/time
   body: { participantId, availabilityTimeRequests: [...] }
   → 200 OK

4. GET /api/rooms/{meetingRoomId}
   → 전체 참가 현황 조회
```

### 시나리오 B — 로그인 사용자 방 생성 및 삭제

```
1. 소셜 로그인 → /callback?token=<JWT> → localStorage 저장

2. POST /api/rooms  (Authorization 헤더 포함)
   → meetingRoomId 저장, 방장(creatorUserId) 설정됨

3. DELETE /api/rooms/{meetingRoomId}  (Authorization 헤더 포함)
   → 방 전체 삭제
```

### 시나리오 C — 초대 코드로 입장

```
1. GET /api/rooms/join-code/AB1-CD2
   → meetingRoomId 확인

2. POST /api/participant
   body: { meetingRoomId, username: "이영희" }
   → participantId 획득

3. POST /api/time
   → 가용 시간 등록
```

### 시나리오 D — 로그인 사용자 재입장 (토큰 있음)

```
1. POST /api/participant  (Authorization: Bearer <token>)
   body: { meetingRoomId: "..." }  ← username 생략 가능
   → 서버가 소셜 닉네임으로 자동 설정
   → 이미 참가 중이면 기존 participantId 반환 (재생성 없음)

2. POST /api/time  (이전 투표 전체 교체)
```

### JWT 에러 처리 패턴

```javascript
async function apiCall(url, options = {}) {
  const token = localStorage.getItem('accessToken');
  if (token) {
    options.headers = { ...options.headers, Authorization: `Bearer ${token}` };
  }

  const res = await fetch(url, options);

  if (res.status === 401) {
    const body = await res.json();
    if (body.errorCode === 'TOKEN_EXPIRED' || body.errorCode === 'INVALID_TOKEN') {
      localStorage.removeItem('accessToken');
      window.location.href = '/login';
      return;
    }
  }

  return res;
}
```
