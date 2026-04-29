package com.chuseok22.timemateserver.meeting.application.repository;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.meeting.core.repository.MeetingRoomRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.repository.MeetingRoomJpaRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

  @Override
  public List<MeetingRoom> findAllByCreatorUserId(UUID creatorUserId) {
    return jpaRepository.findAllByCreatorUserId(creatorUserId);
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    // 존재 여부 확인과 삭제를 하나의 트랜잭션으로 묶어 TOCTOU 경쟁 조건 방지
    if (!jpaRepository.existsById(id)) {
      throw new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND);
    }
    jpaRepository.deleteById(id);
  }
}
