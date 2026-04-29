package com.chuseok22.timemateserver.meeting.infrastructure.repository;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantJpaRepository extends JpaRepository<Participant, UUID> {

  List<Participant> findAllByMeetingRoom(MeetingRoom meetingRoom);

  Participant findByMeetingRoomAndUsername(MeetingRoom meetingRoom, String username);

  // userId로 참가한 방의 Participant 목록 조회
  List<Participant> findAllByUserId(UUID userId);
}
