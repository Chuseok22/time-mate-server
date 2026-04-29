package com.chuseok22.timemateserver.meeting.application.service;

import com.chuseok22.timemateserver.admin.core.service.AdminNotifier;
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
import com.chuseok22.timemateserver.meeting.infrastructure.util.JoinCodeUtil;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeetingRoomServiceImpl implements MeetingRoomService {

  private final MeetingRoomRepository meetingRoomRepository;
  private final MeetingDateService meetingDateService;
  private final MeetingRoomMapper roomMapper;
  private final JoinCodeUtil joinCodeUtil;
  private final JoinCodeProperties joinCodeProperties;
  private final AdminNotifier adminNotifier;

  @Override
  @Transactional
  public RoomInfoResponse createRoom(CreateRoomRequest request) {
    String joinCode = getUniqueJoinCode();
    // 로그인 사용자가 방을 만들면 소유권 부여
    UUID creatorUserId = getAuthenticatedUserId();
    MeetingRoom meetingRoom = MeetingRoom.create(request.getTitle(), joinCode, creatorUserId);
    MeetingRoom savedRoom = meetingRoomRepository.save(meetingRoom);
    List<MeetingDate> meetingDates = meetingDateService.createDate(savedRoom, request.getDates());
    adminNotifier.notifyRoomCreated(meetingRoom.getTitle());
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
    joinCodeUtil.validateBase58Pattern(joinCode);
    MeetingRoom meetingRoom = meetingRoomRepository.findByJoinCode(joinCode);
    List<MeetingDate> meetingDates = meetingDateService.getMeetingDates(meetingRoom);
    return roomMapper.toRoomInfoResponse(meetingRoom, meetingDates);
  }

  @Override
  @Transactional
  public void deleteRoom(UUID roomId) {
    UUID authenticatedUserId = getAuthenticatedUserId();
    MeetingRoom room = meetingRoomRepository.findById(roomId);

    // 비인증 상태이거나 방장이 아닌 경우 삭제 금지
    if (authenticatedUserId == null || !authenticatedUserId.equals(room.getCreatorUserId())) {
      log.warn("방 삭제 권한 없음: roomId={}, requestUserId={}", roomId, authenticatedUserId);
      throw new CustomException(ErrorCode.ROOM_DELETE_FORBIDDEN);
    }
    meetingRoomRepository.deleteById(roomId);
  }

  private UUID getAuthenticatedUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof UsernamePasswordAuthenticationToken
        && auth.getPrincipal() instanceof UUID userId) {
      return userId;
    }
    return null;
  }

  private String getUniqueJoinCode() {
    for (int i = 0; i < joinCodeProperties.maxRetries(); i++) {
      String joinCode = joinCodeUtil.generate();
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
