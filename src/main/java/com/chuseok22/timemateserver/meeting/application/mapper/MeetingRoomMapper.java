package com.chuseok22.timemateserver.meeting.application.mapper;

import com.chuseok22.timemateserver.meeting.application.dto.response.DateAvailabilityResponse;
import com.chuseok22.timemateserver.meeting.application.dto.response.ParticipantInfoResponse;
import com.chuseok22.timemateserver.meeting.application.dto.response.RoomInfoResponse;
import com.chuseok22.timemateserver.meeting.application.dto.response.TimeSlotParticipantsResponse;
import com.chuseok22.timemateserver.meeting.core.constant.TimeSlot;
import com.chuseok22.timemateserver.meeting.core.repository.AvailabilityTimeRepository;
import com.chuseok22.timemateserver.meeting.core.service.ParticipantService;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MeetingRoomMapper {

  private final ParticipantService participantService;
  private final AvailabilityTimeRepository availabilityTimeRepository;

  public RoomInfoResponse toRoomInfoResponse(MeetingRoom meetingRoom, List<MeetingDate> meetingDates) {
    List<LocalDate> dates = meetingDates.stream()
        .map(MeetingDate::getDate)
        .collect(Collectors.toList());
    int participantCount = participantService.countParticipantsByMeetingRoom(meetingRoom);

    List<DateAvailabilityResponse> dateAvailabilityResponses = meetingDates.stream()
        .map(meetingDate -> {
          Map<TimeSlot, List<Participant>> timeSlotParticipantMap = availabilityTimeRepository
              .findParticipantsByMeetingDateGroupWithTimeSlot(meetingDate);

          List<TimeSlotParticipantsResponse> timeSlotParticipantsResponses = timeSlotParticipantMap.entrySet().stream()
              .map(entry -> {
                TimeSlot timeSlot = entry.getKey();
                List<ParticipantInfoResponse> participantInfoResponses = entry.getValue().stream()
                    .map(participant -> new ParticipantInfoResponse(participant.getId(), participant.getUsername()))
                    .collect(Collectors.toList());
                return new TimeSlotParticipantsResponse(timeSlot, participantInfoResponses, participantInfoResponses.size());
              })
              .collect(Collectors.toList());
          return new DateAvailabilityResponse(meetingDate.getDate(), timeSlotParticipantsResponses);
        })
        .collect(Collectors.toList());

    return new RoomInfoResponse(
        meetingRoom.getId(),
        meetingRoom.getTitle(),
        dates,
        participantCount,
        dateAvailabilityResponses
    );
  }
}
