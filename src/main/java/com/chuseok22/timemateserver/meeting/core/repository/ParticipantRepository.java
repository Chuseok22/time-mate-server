package com.chuseok22.timemateserver.meeting.core.repository;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.util.List;
import java.util.UUID;

public interface ParticipantRepository {

  Participant save(Participant participant);

  List<Participant> saveAll(List<Participant> participants);

  Participant findById(UUID participantId);

  Participant findByMeetingRoomAndUsername(MeetingRoom meetingRoom, String username);

  List<Participant> findAllByMeetingRoom(MeetingRoom meetingRoom);
}
