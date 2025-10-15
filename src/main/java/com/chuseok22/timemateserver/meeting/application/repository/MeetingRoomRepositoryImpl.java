package com.chuseok22.timemateserver.meeting.application.repository;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.meeting.core.repository.MeetingRoomRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.repository.MeetingRoomJpaRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MeetingRoomRepositoryImpl implements MeetingRoomRepository {

  private final MeetingRoomJpaRepository jpaRepository;

  @Override
  public MeetingRoom save(MeetingRoom meetingRoom) {
    return jpaRepository.save(meetingRoom);
  }

  @Override
  public MeetingRoom findById(UUID id) {
    return jpaRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND));
  }

  @Override
  public MeetingRoom findByJoinCode(String joinCode) {
    return jpaRepository.findByJoinCode(joinCode)
        .orElseThrow(() -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND));
  }

  @Override
  public boolean existsByJoinCode(String joinCode) {
    return jpaRepository.existsByJoinCode(joinCode);
  }
}
