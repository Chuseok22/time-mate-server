package com.chuseok22.timemateserver.meeting.application.service;

import com.chuseok22.timemateserver.meeting.core.repository.MeetingDateRepository;
import com.chuseok22.timemateserver.meeting.core.service.MeetingDateService;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeetingDateServiceImpl implements MeetingDateService {

  private final MeetingDateRepository meetingDateRepository;

  @Override
  @Transactional
  public List<MeetingDate> createDate(MeetingRoom room, List<LocalDate> dates) {
    List<MeetingDate> meetingDates = dates.stream()
        .distinct()
        .map(date -> MeetingDate.create(room, date))
        .collect(Collectors.toList());
    return meetingDateRepository.saveAll(meetingDates);
  }

  @Override
  public List<MeetingDate> getMeetingDates(MeetingRoom room) {
    return meetingDateRepository.findAllByMeetingRoom(room);
  }
}
