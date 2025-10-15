package com.chuseok22.timemateserver.meeting.core.repository;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import java.util.UUID;

public interface MeetingRoomRepository {

  MeetingRoom save(MeetingRoom meetingRoom);

  MeetingRoom findById(UUID id);

  MeetingRoom findByJoinCode(String joinCode);

  boolean existsByJoinCode(String joinCode);
}
