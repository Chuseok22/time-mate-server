package com.chuseok22.timemateserver.meeting.application.mapper;

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

  public RoomInfoResponse toRoomInfoResponse(MeetingRoom meetingRoom, List<MeetingDate> meetingDates) {
    List<LocalDate> dates = meetingDates.stream()
        .map(MeetingDate::getDate)
        .collect(Collectors.toList());
    int participantCount = participantService.countParticipantsByMeetingRoom(meetingRoom);
    return new RoomInfoResponse(
        meetingRoom.getId(),
        meetingRoom.getTitle(),
        dates,
        participantCount
    );
  }
}
