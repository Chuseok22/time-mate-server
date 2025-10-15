package com.chuseok22.timemateserver.meeting.application.service;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.meeting.application.dto.request.CreateRoomRequest;
import com.chuseok22.timemateserver.meeting.application.dto.response.RoomInfoResponse;
import com.chuseok22.timemateserver.meeting.application.mapper.MeetingRoomMapper;
import com.chuseok22.timemateserver.meeting.core.repository.MeetingRoomRepository;
import com.chuseok22.timemateserver.meeting.core.service.MeetingDateService;
import com.chuseok22.timemateserver.meeting.core.service.MeetingRoomService;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.properties.JoinCodeProperties;
import com.chuseok22.timemateserver.meeting.infrastructure.util.JoinCodeGenerator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeetingRoomServiceImpl implements MeetingRoomService {

  private final MeetingRoomRepository meetingRoomRepository;
  private final MeetingDateService meetingDateService;
  private final MeetingRoomMapper roomMapper;
  private final JoinCodeGenerator joinCodeGenerator;
  private final JoinCodeProperties joinCodeProperties;

  @Override
  @Transactional
  public RoomInfoResponse createRoom(CreateRoomRequest request) {
    String joinCode = getUniqueJoinCode();
    MeetingRoom meetingRoom = MeetingRoom.create(request.getTitle(), joinCode);
    MeetingRoom savedRoom = meetingRoomRepository.save(meetingRoom);
    List<MeetingDate> meetingDates = meetingDateService.createDate(savedRoom, request.getDates());
    return roomMapper.toRoomInfoResponse(savedRoom, meetingDates);
  }

  @Override
  @Transactional(readOnly = true)
  public RoomInfoResponse getRoomInfo(UUID roomId) {
    MeetingRoom meetingRoom = meetingRoomRepository.findById(roomId);
    List<MeetingDate> meetingDates = meetingDateService.getMeetingDates(meetingRoom);
    return roomMapper.toRoomInfoResponse(meetingRoom, meetingDates);
  }

  @Override
  @Transactional(readOnly = true)
  public RoomInfoResponse getRoomInfoByJoinCode(String joinCode) {
    MeetingRoom meetingRoom = meetingRoomRepository.findByJoinCode(joinCode);
    List<MeetingDate> meetingDates = meetingDateService.getMeetingDates(meetingRoom);
    return roomMapper.toRoomInfoResponse(meetingRoom, meetingDates);
  }

  private String getUniqueJoinCode() {
    for (int i = 0; i < joinCodeProperties.maxRetries(); i++) {
      String joinCode = joinCodeGenerator.generate();
      boolean exists = meetingRoomRepository.existsByJoinCode(joinCode);
      if (exists) {
        continue;
      }
      return joinCode;
    }
    log.warn("방 참가 코드 생성 최대 횟수 {}회를 초과했습니다", joinCodeProperties.maxRetries());
    throw new CustomException(ErrorCode.JOIN_CODE_DUPLICATE);
  }
}
