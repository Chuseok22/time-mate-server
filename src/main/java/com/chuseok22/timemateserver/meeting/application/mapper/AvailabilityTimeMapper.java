package com.chuseok22.timemateserver.meeting.application.mapper;

import com.chuseok22.timemateserver.meeting.application.dto.response.DateAvailabilityResponse;
import com.chuseok22.timemateserver.meeting.application.dto.response.TimeSlotParticipantsResponse;
import com.chuseok22.timemateserver.meeting.core.constant.TimeSlot;
import com.chuseok22.timemateserver.meeting.core.repository.AvailabilityTimeRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AvailabilityTimeMapper {

  private final AvailabilityTimeRepository availabilityTimeRepository;
  private final TimeSlotMapper timeSlotMapper;

  public DateAvailabilityResponse toDateAvailabilityResponse(MeetingDate meetingDate) {
    Map<TimeSlot, List<Participant>> timeSlotParticipantMap =
        availabilityTimeRepository.findParticipantsByMeetingDateGroupWithTimeSlot(meetingDate);

    List<TimeSlotParticipantsResponse> timeSlotParticipantsResponses =
        timeSlotMapper.toTimeSlotParticipantsResponses(timeSlotParticipantMap);

    return new DateAvailabilityResponse(
        meetingDate.getDate(),
        timeSlotParticipantsResponses
    );
  }

  public List<DateAvailabilityResponse> toDateAvailabilityResponses(List<MeetingDate> meetingDates) {
    return meetingDates.stream()
        .map(this::toDateAvailabilityResponse)
        .collect(Collectors.toList());
  }
}
