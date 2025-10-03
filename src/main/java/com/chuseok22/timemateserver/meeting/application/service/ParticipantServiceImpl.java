package com.chuseok22.timemateserver.meeting.application.service;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.meeting.application.dto.request.CreateParticipantRequest;
import com.chuseok22.timemateserver.meeting.core.repository.MeetingRoomRepository;
import com.chuseok22.timemateserver.meeting.core.repository.ParticipantRepository;
import com.chuseok22.timemateserver.meeting.core.service.ParticipantService;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

  private final ParticipantRepository participantRepository;
  private final MeetingRoomRepository meetingRoomRepository;

  @Override
  @Transactional
  public void createParticipant(CreateParticipantRequest request) {
    MeetingRoom room = meetingRoomRepository.findById(request.getMeetingRoomId());
    duplicateUsername(room, request.getUsername());
    participantRepository.save(Participant.create(room, request.getUsername(), request.getPassword()));
  }

  @Override
  @Transactional(readOnly = true)
  public int countParticipantsByMeetingRoom(MeetingRoom meetingRoom) {
    return participantRepository.findAllByMeetingRoom(meetingRoom).size();
  }

  private void duplicateUsername(MeetingRoom room, String username) {
    if (participantRepository.existsByMeetingRoomAndUsername(room, username)) {
      log.error("Room: {}에 username: {}이 이미 존재합니다.", room.getId(), username);
      throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
    }
  }
}
