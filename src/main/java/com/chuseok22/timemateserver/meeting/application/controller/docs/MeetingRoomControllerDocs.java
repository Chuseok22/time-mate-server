package com.chuseok22.timemateserver.meeting.application.controller.docs;

import com.chuseok22.timemateserver.meeting.application.dto.request.CreateRoomRequest;
import com.chuseok22.timemateserver.meeting.application.dto.response.RoomInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import com.chuseok22.apichangelog.annotation.ApiChangeLog;
import com.chuseok22.apichangelog.annotation.ApiChangeLogs;
import org.springframework.http.ResponseEntity;

@Tag(name = "미팅룸", description = "미팅룸 생성 및 조회 API")
public interface MeetingRoomControllerDocs {

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025-10-15",
          author = "Chuseok22",
          description = "joinCode 응답 필드 추가",
          issueUrl = "https://github.com/Chuseok22/meet-time-server/issues/26"
      ),
      @ApiChangeLog(
          date = "2025-10-02",
          author = "Chuseok22",
          description = "미팅룸 생성 API 구현",
          issueUrl = "https://github.com/Chuseok22/meet-time-server/issues/7"
      )
  })
  @Operation(
      summary = "미팅룸 생성",
      description = """
          ### 요청 파라미터
          - `title` (String, 필수): 미팅룸 제목
          - `dates` (List\\<LocalDate\\>, 필수): 후보 날짜 목록 (YYYY-MM-DD 형식)

          ### 응답 데이터
          - `meetingRoomId` (UUID): 생성된 미팅룸 ID
          - `title` (String): 미팅룸 제목
          - `joinCode` (String): 참가자 초대용 방 코드
          - `dates` (List\\<String\\>): 후보 날짜 목록
          - `participantsCount` (int): 현재 참가자 수
          - `participantInfoResponses` (List): 참가자 정보 목록
          - `dateAvailabilityResponses` (List): 날짜별 시간대별 참가 가능 현황

          ### 유의 사항
          - 방 생성 시 고유한 `joinCode`가 자동 발급됩니다.
          - `dates`는 중복 날짜가 포함되어도 서버에서 자동으로 중복 제거됩니다.
          """
  )
  ResponseEntity<RoomInfoResponse> createRoom(CreateRoomRequest request);

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025-10-14",
          author = "Chuseok22",
          description = "응답에 참가자 정보 목록 및 날짜별 가용 시간 현황 추가",
          issueUrl = "https://github.com/Chuseok22/meet-time-server/issues/24"
      ),
      @ApiChangeLog(
          date = "2025-10-02",
          author = "Chuseok22",
          description = "미팅룸 ID 조회 API 구현",
          issueUrl = "https://github.com/Chuseok22/meet-time-server/issues/7"
      )
  })
  @Operation(
      summary = "미팅룸 조회 (ID)",
      description = """
          ### 경로 파라미터
          - `room-id` (UUID, 필수): 조회할 미팅룸 ID

          ### 응답 데이터
          - `meetingRoomId` (UUID): 미팅룸 ID
          - `title` (String): 미팅룸 제목
          - `joinCode` (String): 참가자 초대용 방 코드
          - `dates` (List\\<String\\>): 후보 날짜 목록
          - `participantsCount` (int): 현재 참가자 수
          - `participantInfoResponses` (List): 참가자 정보 목록
            - `participantId` (UUID): 참가자 ID
            - `username` (String): 참가자 이름
          - `dateAvailabilityResponses` (List): 날짜별 시간대별 참가 가능 현황
            - `date` (String): 날짜 (YYYY-MM-DD)
            - `timeSlotParticipantsResponses` (List): 시간대별 참가 가능 현황
              - `timeSlot` (String): 시간대 (예: SLOT_09_00 → 09:00)
              - `participantInfoResponses` (List): 해당 시간대 참가 가능 참가자 목록
              - `availabilityCount` (int): 해당 시간대 참가 가능 인원 수

          ### 유의 사항
          - 존재하지 않는 미팅룸 ID 조회 시 404 에러가 반환됩니다.
          """
  )
  ResponseEntity<RoomInfoResponse> getRoom(
      @Parameter(description = "미팅룸 ID", required = true) UUID id
  );

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025-10-15",
          author = "Chuseok22",
          description = "방 코드를 통한 미팅룸 조회 API 구현",
          issueUrl = "https://github.com/Chuseok22/meet-time-server/issues/26"
      )
  })
  @Operation(
      summary = "미팅룸 조회 (방 코드)",
      description = """
          ### 경로 파라미터
          - `join-code` (String, 필수): 참가자 초대용 방 코드 (예: AB1-CD2)

          ### 응답 데이터
          - `meetingRoomId` (UUID): 미팅룸 ID
          - `title` (String): 미팅룸 제목
          - `joinCode` (String): 참가자 초대용 방 코드
          - `dates` (List\\<String\\>): 후보 날짜 목록
          - `participantsCount` (int): 현재 참가자 수
          - `participantInfoResponses` (List): 참가자 정보 목록
          - `dateAvailabilityResponses` (List): 날짜별 시간대별 참가 가능 현황

          ### 유의 사항
          - 방 코드는 대소문자를 구분합니다.
          - 존재하지 않는 방 코드 조회 시 404 에러가 반환됩니다.
          """
  )
  ResponseEntity<RoomInfoResponse> getRoomByJoinCode(
      @Parameter(description = "참가자 초대용 방 코드", required = true) String joinCode
  );

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2026-04-30",
          author = "Chuseok22",
          description = "방 삭제 API 추가 (방장 전용)",
          issueUrl = "https://github.com/Chuseok22/time-mate-server/issues/33"
      )
  })
  @Operation(
      summary = "방 삭제",
      description = """
          ### 요청 파라미터
          - `room-id` (UUID, 필수): 삭제할 방 ID

          ### 유의 사항
          - 방을 만든 로그인 사용자만 삭제 가능
          - 방 전체 삭제 (참가자, 투표 포함). 복구 불가
          - 방장이 아닌 경우 403 ROOM_DELETE_FORBIDDEN 반환
          - Guest가 만든 방은 삭제 불가
          """
  )
  ResponseEntity<Void> deleteRoom(
      @Parameter(description = "방 ID", required = true) UUID roomId);
}
