package com.chuseok22.timemateserver.meeting.application.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RoomInfoResponse(
    UUID meetingRoomId,
    String title,
    List<LocalDate> dates,
    int participantsCount,
    List<ParticipantInfoResponse> participantInfoResponses,
    List<DateAvailabilityResponse> dateAvailabilityResponses
) {

}
