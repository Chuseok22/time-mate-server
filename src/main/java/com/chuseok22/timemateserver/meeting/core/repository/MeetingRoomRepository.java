package com.chuseok22.timemateserver.meeting.core.repository;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import java.util.List;
import java.util.UUID;

public interface MeetingRoomRepository {

  MeetingRoom save(MeetingRoom meetingRoom);

  MeetingRoom findById(UUID id);

  MeetingRoom findByJoinCode(String joinCode);

  boolean existsByJoinCode(String joinCode);

  // 특정 소셜 사용자가 생성한 방 목록 조회
  List<MeetingRoom> findAllByCreatorUserId(UUID creatorUserId);

  void deleteById(UUID id);
}
