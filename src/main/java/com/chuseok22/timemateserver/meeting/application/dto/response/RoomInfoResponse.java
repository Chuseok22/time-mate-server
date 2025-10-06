package com.chuseok22.timemateserver.meeting.application.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RoomInfoResponse(
    UUID meetingRoomId,
    String title,
    List<LocalDate> dates,
    int participantsCount

    // 방 점보에는 언제 누가 가능한지 정보 추가
) {

}
