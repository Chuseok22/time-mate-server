package com.chuseok22.timemateserver.meeting.core.service;

import com.chuseok22.timemateserver.meeting.application.dto.request.CreateParticipantRequest;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;

public interface ParticipantService {

  void createParticipant(CreateParticipantRequest request);

  int countParticipantsByMeetingRoom(MeetingRoom meetingRoom);
}
