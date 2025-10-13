package com.chuseok22.timemateserver.meeting.application.repository;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.meeting.core.repository.MeetingDateRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.repository.MeetingDateJpaRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MeetingDateRepositoryImpl implements MeetingDateRepository {

  private final MeetingDateJpaRepository jpaRepository;

  @Override
  public MeetingDate save(MeetingDate meetingDate) {
    return jpaRepository.save(meetingDate);
  }

  @Override
  public List<MeetingDate> saveAll(List<MeetingDate> meetingDates) {
    return jpaRepository.saveAll(meetingDates);
  }

  @Override
  public List<MeetingDate> findAllByMeetingRoom(MeetingRoom room) {
    return jpaRepository.findAllByMeetingRoom(room);
  }

  @Override
  public MeetingDate findOptionalByMeetingRoomAndDate(MeetingRoom room, LocalDate date) {
    return jpaRepository.findByMeetingRoomAndDate(room, date)
        .orElseThrow(() -> {
          log.error("Room: {}, Date: {}에 해당하는 MeetingDate를 찾을 수 없습니다.", room.getId(), date);
          return new CustomException(ErrorCode.MEETING_DATE_NOT_FOUND);
        });
  }
}
