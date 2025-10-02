package com.chuseok22.timemateserver.meeting.application.service;

import com.chuseok22.timemateserver.meeting.application.dto.request.CreateRoomRequest;
import com.chuseok22.timemateserver.meeting.application.dto.response.RoomInfoResponse;
import com.chuseok22.timemateserver.meeting.application.mapper.MeetingRoomMapper;
import com.chuseok22.timemateserver.meeting.core.repository.MeetingRoomRepository;
import com.chuseok22.timemateserver.meeting.core.service.MeetingDateService;
import com.chuseok22.timemateserver.meeting.core.service.MeetingRoomService;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
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

  @Override
  @Transactional
  public RoomInfoResponse createRoom(CreateRoomRequest request) {
    MeetingRoom meetingRoom = MeetingRoom.create(request.getTitle());
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
}
