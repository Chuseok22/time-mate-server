package com.chuseok22.timemateserver.meeting.core.service;

import com.chuseok22.timemateserver.meeting.application.dto.request.CreateRoomRequest;
import com.chuseok22.timemateserver.meeting.application.dto.response.RoomInfoResponse;
import java.util.UUID;

public interface MeetingRoomService {

  RoomInfoResponse createRoom(CreateRoomRequest request);

  RoomInfoResponse getRoomInfo(UUID roomId);
}
