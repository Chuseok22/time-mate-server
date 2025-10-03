package com.chuseok22.timemateserver.meeting.application.dto.response;

import java.util.UUID;

public record ParticipantInfoResponse(
    UUID participantId,
    String username
) {

}
