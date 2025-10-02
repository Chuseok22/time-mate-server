package com.chuseok22.timemateserver.meeting.core.repository;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.util.List;

public interface ParticipantRepository {

  Participant save(Participant participant);

  List<Participant> saveAll(List<Participant> participants);

  List<Participant> findAllByMeetingRoom(MeetingRoom meetingRoom);
}
