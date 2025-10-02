package com.chuseok22.timemateserver.meeting.application.controller;

import com.chuseok22.timemateserver.common.application.aop.LogMonitoringInvocation;
import com.chuseok22.timemateserver.meeting.application.dto.request.CreateRoomRequest;
import com.chuseok22.timemateserver.meeting.application.dto.response.RoomInfoResponse;
import com.chuseok22.timemateserver.meeting.core.service.MeetingRoomService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class MeetingRoomController {

  private final MeetingRoomService meetingRoomService;

  @LogMonitoringInvocation
  @PostMapping()
  public ResponseEntity<RoomInfoResponse> createRoom(
      @Valid @RequestBody CreateRoomRequest request
  ) {
    return ResponseEntity.ok(meetingRoomService.createRoom(request));
  }

  @LogMonitoringInvocation
  @GetMapping("/{room-id}")
  public ResponseEntity<RoomInfoResponse> getRoom(
      @PathVariable(name = "room-id") UUID id
  ) {
    return ResponseEntity.ok(meetingRoomService.getRoomInfo(id));
  }
}
