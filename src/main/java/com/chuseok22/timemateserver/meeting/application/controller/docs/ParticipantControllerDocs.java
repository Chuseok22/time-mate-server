package com.chuseok22.timemateserver.meeting.application.controller.docs;

import com.chuseok22.timemateserver.meeting.application.dto.request.ParticipantLoginRequest;
import com.chuseok22.timemateserver.meeting.application.dto.response.ParticipantInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import com.chuseok22.apichangelog.annotation.ApiChangeLog;
import com.chuseok22.apichangelog.annotation.ApiChangeLogs;
import org.springframework.http.ResponseEntity;

@Tag(name = "참가자", description = "참가자 로그인 및 조회 API")
public interface ParticipantControllerDocs {

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025-10-04",
          author = "Chuseok22",
          description = "참가자 로그인 API 구현",
          issueUrl = "https://github.com/Chuseok22/meet-time-server/issues/9"
      )
  })
  @Operation(
      summary = "참가자 로그인",
      description = """
          ### 개요
          미팅룸에 참가자로 입장합니다. 같은 방에서 동일한 이름이 없으면 신규 생성되고, 존재하면 비밀번호 검증 후 기존 참가자 정보를 반환합니다.

          ### 요청 파라미터
          - `meetingRoomId` (UUID, 필수): 입장할 미팅룸 ID
          - `username` (String, 필수): 참가자 이름
          - `password` (String, 선택): 참가자 비밀번호 (설정된 경우 일치해야 입장 가능)

          ### 응답 데이터
          - `participantId` (UUID): 참가자 ID
          - `username` (String): 참가자 이름

          ### 유의 사항
          - 동일한 미팅룸 내에서 같은 이름으로 재입장 시 기존 참가자 ID가 반환됩니다.
          - 비밀번호가 설정된 참가자는 올바른 비밀번호 없이 입장할 수 없습니다.
          - 존재하지 않는 미팅룸 ID 입력 시 404 에러가 반환됩니다.
          """
  )
  ResponseEntity<ParticipantInfoResponse> createParticipant(ParticipantLoginRequest request);

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025-10-04",
          author = "Chuseok22",
          description = "참가자 정보 조회 API 구현",
          issueUrl = "https://github.com/Chuseok22/meet-time-server/issues/9"
      )
  })
  @Operation(
      summary = "참가자 정보 조회",
      description = """
          ### 경로 파라미터
          - `participant-id` (UUID, 필수): 조회할 참가자 ID

          ### 응답 데이터
          - `participantId` (UUID): 참가자 ID
          - `username` (String): 참가자 이름

          ### 유의 사항
          - 존재하지 않는 참가자 ID 조회 시 404 에러가 반환됩니다.
          """
  )
  ResponseEntity<ParticipantInfoResponse> getParticipant(
      @Parameter(description = "참가자 ID", required = true) UUID participantId
  );

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2026-04-30",
          author = "Chuseok22",
          description = "방 탈퇴 API 추가 (본인 전용)",
          issueUrl = "https://github.com/Chuseok22/time-mate-server/issues/33"
      )
  })
  @Operation(
      summary = "방 탈퇴",
      description = """
          ### 요청 파라미터
          - `participant-id` (UUID, 필수): 탈퇴할 참가자 ID

          ### 유의 사항
          - 본인 참가 기록만 삭제 가능 (userId 일치 확인)
          - Guest 참가자는 탈퇴 불가 (userId가 없으므로 403 반환)
          - 투표 기록 포함 삭제
          """
  )
  ResponseEntity<Void> deleteParticipant(
      @Parameter(description = "참가자 ID", required = true) UUID participantId);
}
