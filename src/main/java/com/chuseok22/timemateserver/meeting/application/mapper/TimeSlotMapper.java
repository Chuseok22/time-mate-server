package com.chuseok22.timemateserver.meeting.application.mapper;

import com.chuseok22.timemateserver.meeting.application.dto.response.ParticipantInfoResponse;
import com.chuseok22.timemateserver.meeting.application.dto.response.TimeSlotParticipantsResponse;
import com.chuseok22.timemateserver.meeting.core.constant.TimeSlot;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TimeSlotMapper {

  private final ParticipantMapper participantMapper;

  public TimeSlotParticipantsResponse toTimeSlotParticipantsResponse(TimeSlot timeSlot, List<Participant> participants) {
    List<ParticipantInfoResponse> participantInfoResponses =
        participantMapper.toParticipantInfoResponses(participants);

    return new TimeSlotParticipantsResponse(timeSlot, participantInfoResponses, participantInfoResponses.size());
  }

  public List<TimeSlotParticipantsResponse> toTimeSlotParticipantsResponses(Map<TimeSlot, List<Participant>> timeSlotParticipantMap) {
    return timeSlotParticipantMap.entrySet().stream()
        .map(entry -> toTimeSlotParticipantsResponse(entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(response -> response.timeSlot().getStartTime()))
        .collect(Collectors.toList());
  }
}
