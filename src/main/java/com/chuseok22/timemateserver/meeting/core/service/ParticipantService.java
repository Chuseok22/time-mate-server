package com.chuseok22.timemateserver.meeting.core.service;

import com.chuseok22.timemateserver.meeting.application.dto.request.ParticipantLoginRequest;
import com.chuseok22.timemateserver.meeting.application.dto.response.ParticipantInfoResponse;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import java.util.UUID;

public interface ParticipantService {

  ParticipantInfoResponse loginParticipant(ParticipantLoginRequest request);

  ParticipantInfoResponse getParticipantInfo(UUID participantId);

  int countParticipantsByMeetingRoom(MeetingRoom meetingRoom);
}
