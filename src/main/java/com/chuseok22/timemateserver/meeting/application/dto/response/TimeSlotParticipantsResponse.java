package com.chuseok22.timemateserver.meeting.application.dto.response;

import com.chuseok22.timemateserver.meeting.core.constant.TimeSlot;
import java.util.List;

public record TimeSlotParticipantsResponse(
    TimeSlot timeSlot,
    List<ParticipantInfoResponse> participantInfoResponses
) {

}
