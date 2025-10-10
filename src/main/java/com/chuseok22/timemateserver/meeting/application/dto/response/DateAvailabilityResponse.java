package com.chuseok22.timemateserver.meeting.application.dto.response;

import java.time.LocalDate;
import java.util.List;

public record DateAvailabilityResponse(
    LocalDate localDate,
    List<TimeSlotParticipantsResponse> timeSlotParticipantsResponses
) {

}
