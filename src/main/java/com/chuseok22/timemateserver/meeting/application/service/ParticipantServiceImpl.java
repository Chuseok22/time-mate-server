package com.chuseok22.timemateserver.meeting.application.service;

import com.chuseok22.timemateserver.meeting.core.repository.ParticipantRepository;
import com.chuseok22.timemateserver.meeting.core.service.ParticipantService;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

  private final ParticipantRepository participantRepository;

  @Override
  @Transactional(readOnly = true)
  public int countParticipantsByMeetingRoom(MeetingRoom meetingRoom) {
    return participantRepository.findAllByMeetingRoom(meetingRoom).size();
  }
}
