package com.chuseok22.timemateserver.meeting.application.mapper;

import com.chuseok22.timemateserver.meeting.application.dto.response.ParticipantInfoResponse;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ParticipantMapper {

  public ParticipantInfoResponse toParticipantInfoResponse(Participant participant) {
    return new ParticipantInfoResponse(
        participant.getId(),
        participant.getUsername()
    );
  }

  public List<ParticipantInfoResponse> toParticipantInfoResponses(List<Participant> participants) {
    return participants.stream()
        .map(this::toParticipantInfoResponse)
        .collect(Collectors.toList());
  }
}
