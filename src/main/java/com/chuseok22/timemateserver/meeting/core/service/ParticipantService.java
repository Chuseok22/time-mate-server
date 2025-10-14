package com.chuseok22.timemateserver.meeting.core.service;

import com.chuseok22.timemateserver.meeting.application.dto.request.ParticipantLoginRequest;
import com.chuseok22.timemateserver.meeting.application.dto.response.ParticipantInfoResponse;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.util.List;
import java.util.UUID;

public interface ParticipantService {

  ParticipantInfoResponse loginParticipant(ParticipantLoginRequest request);

  ParticipantInfoResponse getParticipantInfo(UUID participantId);

  List<Participant> findAllByMeetingRoom(MeetingRoom meetingRoom);
}
