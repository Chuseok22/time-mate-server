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

  // 특정 소셜 사용자가 참가한 방의 Participant 목록 조회
  List<Participant> findAllByUserId(UUID userId);
}
