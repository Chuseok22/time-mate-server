package com.chuseok22.timemateserver.meeting.application.repository;

import com.chuseok22.timemateserver.meeting.core.constant.TimeSlot;
import com.chuseok22.timemateserver.meeting.core.repository.AvailabilityTimeRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.AvailabilityTime;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import com.chuseok22.timemateserver.meeting.infrastructure.repository.AvailabilityTimeJpaRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AvailabilityTimeRepositoryImpl implements AvailabilityTimeRepository {

  private final AvailabilityTimeJpaRepository jpaRepository;

  @Override
  public List<AvailabilityTime> saveAll(List<AvailabilityTime> availabilityTimes) {
    return jpaRepository.saveAll(availabilityTimes);
  }

  @Override
  public void deleteAllByParticipant(Participant participant) {
    jpaRepository.deleteAllByParticipant(participant);
  }

  @Override
  public List<AvailabilityTime> findAllByMeetingDate(MeetingDate meetingDate) {
    return jpaRepository.findAllByMeetingDate(meetingDate);
  }

  @Override
  public Map<TimeSlot, List<Participant>> findParticipantsByMeetingDateGroupWithTimeSlot(MeetingDate meetingDate) {
    List<AvailabilityTime> availabilityTimes = jpaRepository.findAllByMeetingDate(meetingDate);

    Map<TimeSlot, List<Participant>> timeSlotParticipantsMap = Arrays.stream(TimeSlot.values())
        .collect(Collectors.toMap(
            timeSlot -> timeSlot,
            timeSlot -> new ArrayList<>()
        ));

    Map<TimeSlot, List<AvailabilityTime>> groupedByTimeSlot = availabilityTimes.stream()
        .collect(Collectors.groupingBy(AvailabilityTime::getTimeSlot));

    groupedByTimeSlot.forEach((timeSlot, availabilityTimeList) -> {
      List<Participant> participants = availabilityTimeList.stream()
          .map(AvailabilityTime::getParticipant)
          .sorted(Comparator.comparing(Participant::getId))
          .collect(Collectors.toList());
      timeSlotParticipantsMap.put(timeSlot, participants);
    });
    return timeSlotParticipantsMap;
  }
}
