package com.chuseok22.timemateserver.meeting.core.service;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;

public interface ParticipantService {

  int countParticipantsByMeetingRoom(MeetingRoom meetingRoom);
}
