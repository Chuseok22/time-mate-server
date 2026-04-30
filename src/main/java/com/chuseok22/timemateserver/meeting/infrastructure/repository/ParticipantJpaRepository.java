package com.chuseok22.timemateserver.meeting.infrastructure.repository;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantJpaRepository extends JpaRepository<Participant, UUID> {

  List<Participant> findAllByMeetingRoom(MeetingRoom meetingRoom);

  Participant findByMeetingRoomAndUsername(MeetingRoom meetingRoom, String username);

  // userId로 참가한 방의 Participant 목록 조회 — meetingRoom LAZY 로딩 N+1 방지
  @EntityGraph(attributePaths = "meetingRoom")
  List<Participant> findAllByUserId(UUID userId);

  // 특정 방에서 특정 userId를 가진 참가자 단건 조회
  Participant findByMeetingRoomAndUserId(MeetingRoom meetingRoom, UUID userId);
}
