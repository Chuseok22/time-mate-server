package com.chuseok22.timemateserver.meeting.application.repository;

import com.chuseok22.timemateserver.meeting.core.repository.MeetingDateRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.repository.MeetingDateJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
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
}
