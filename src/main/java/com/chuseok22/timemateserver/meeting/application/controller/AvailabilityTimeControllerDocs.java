package com.chuseok22.timemateserver.meeting.application.controller;

import com.chuseok22.apichangelog.annotation.ApiChangeLog;
import com.chuseok22.apichangelog.annotation.ApiChangeLogs;
import com.chuseok22.timemateserver.meeting.application.dto.request.UpsertAvailabilityRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "가용 시간", description = "참가자별 가용 시간 설정 API")
public interface AvailabilityTimeControllerDocs {

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2025-10-05",
          author = "Chuseok22",
          description = "참가자별 참석 가능 시간 설정 API 구현",
          issueUrl = "https://github.com/Chuseok22/meet-time-server/issues/17"
      )
  })
  @Operation(
      summary = "가용 시간 설정",
      description = """
          ### 개요
          참가자가 날짜별로 참석 가능한 시간대를 등록합니다. 기존에 등록된 가용 시간은 전체 교체됩니다 (Upsert).

          ### 요청 파라미터
          - `participantId` (UUID, 필수): 가용 시간을 설정할 참가자 ID
          - `availabilityTimeRequests` (List, 필수): 날짜별 가용 시간 목록
            - `date` (LocalDate, 필수): 날짜 (YYYY-MM-DD 형식)
            - `timeSlots` (List\\<TimeSlot\\>, 최대 32개): 해당 날짜의 참석 가능 시간대 목록
              - 사용 가능한 값: `SLOT_08_00` ~ `SLOT_23_30` (30분 간격)
              - 예시: `["SLOT_09_00", "SLOT_09_30", "SLOT_10_00"]`

          ### 응답 데이터
          `없음` (200 OK)

          ### 유의 사항
          - 기존에 등록된 해당 참가자의 가용 시간은 모두 삭제 후 새로운 데이터로 저장됩니다.
          - `timeSlots`를 빈 배열로 전송하면 해당 날짜의 가용 시간이 초기화됩니다.
          - `timeSlots`는 날짜별로 최대 32개까지 등록 가능합니다.
          - 존재하지 않는 참가자 ID 입력 시 404 에러가 반환됩니다.
          """
  )
  ResponseEntity<Void> setAvailabilityTime(UpsertAvailabilityRequest request);
}
