package com.chuseok22.timemateserver.meeting.application.mapper;

import com.chuseok22.timemateserver.meeting.application.dto.response.DateAvailabilityResponse;
import com.chuseok22.timemateserver.meeting.application.dto.response.RoomInfoResponse;
import com.chuseok22.timemateserver.meeting.core.service.ParticipantService;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MeetingRoomMapper {

  private final ParticipantService participantService;
  private final AvailabilityTimeMapper availabilityTimeMapper;

  public RoomInfoResponse toRoomInfoResponse(MeetingRoom meetingRoom, List<MeetingDate> meetingDates) {
    List<LocalDate> dates = extractDatesFromMeetingDates(meetingDates);
    int participantCount = participantService.countParticipantsByMeetingRoom(meetingRoom);

    List<DateAvailabilityResponse> dateAvailabilityResponses = availabilityTimeMapper.toDateAvailabilityResponses(meetingDates);

    return new RoomInfoResponse(
        meetingRoom.getId(),
        meetingRoom.getTitle(),
        dates,
        participantCount,
        dateAvailabilityResponses
    );
  }

  private List<LocalDate> extractDatesFromMeetingDates(List<MeetingDate> meetingDates) {
    return meetingDates.stream()
        .map(MeetingDate::getDate)
        .sorted()
        .collect(Collectors.toList());
  }
}
