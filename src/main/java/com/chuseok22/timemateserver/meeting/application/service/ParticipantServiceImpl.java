package com.chuseok22.timemateserver.meeting.application.service;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.common.core.util.CommonUtil;
import com.chuseok22.timemateserver.meeting.application.dto.request.ParticipantLoginRequest;
import com.chuseok22.timemateserver.meeting.application.dto.response.ParticipantInfoResponse;
import com.chuseok22.timemateserver.meeting.application.mapper.ParticipantMapper;
import com.chuseok22.timemateserver.meeting.core.repository.MeetingRoomRepository;
import com.chuseok22.timemateserver.meeting.core.repository.ParticipantRepository;
import com.chuseok22.timemateserver.meeting.core.service.ParticipantService;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.util.UUID;
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
  private final ParticipantMapper participantMapper;

  @Override
  @Transactional
  public ParticipantInfoResponse loginParticipant(ParticipantLoginRequest request) {
    MeetingRoom room = meetingRoomRepository.findById(request.getMeetingRoomId());
    Participant participant = participantRepository.findByMeetingRoomAndUsername(room, request.getUsername());
    if (participant != null) {
      validatePassword(participant, request.getPassword());
      return participantMapper.toParticipantInfoResponse(participant);
    }
    Participant savedParticipant = participantRepository.save(Participant.create(room, request.getUsername(), request.getPassword()));
    return participantMapper.toParticipantInfoResponse(savedParticipant);
  }

  @Override
  @Transactional(readOnly = true)
  public ParticipantInfoResponse getParticipantInfo(UUID participantId) {
    Participant participant = participantRepository.findById(participantId);
    return participantMapper.toParticipantInfoResponse(participant);
  }

  @Override
  @Transactional(readOnly = true)
  public int countParticipantsByMeetingRoom(MeetingRoom meetingRoom) {
    return participantRepository.findAllByMeetingRoom(meetingRoom).size();
  }

  private void validatePassword(Participant participant, String password) {
    if (!CommonUtil.nvl(participant.getPassword(), "").isEmpty()) {
      if (!participant.getPassword().equals(password.trim())) {
        log.error("사용자: {}의 비밀번호가 일치하지 않습니다.", participant.getId());
        throw new CustomException(ErrorCode.INVALID_PASSWORD);
      }
    }
  }
}
