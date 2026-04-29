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
import com.chuseok22.timemateserver.user.core.repository.UserRepository;
import com.chuseok22.timemateserver.user.infrastructure.entity.User;
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
public class ParticipantServiceImpl implements ParticipantService {

  private final ParticipantRepository participantRepository;
  private final MeetingRoomRepository meetingRoomRepository;
  private final ParticipantMapper participantMapper;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ParticipantInfoResponse loginParticipant(ParticipantLoginRequest request) {
    MeetingRoom room = meetingRoomRepository.findById(request.getMeetingRoomId());
    UUID authenticatedUserId = getAuthenticatedUserId();

    if (authenticatedUserId != null) {
      return handleAuthenticatedUser(request, room, authenticatedUserId);
    }
    return handleGuestUser(request, room);
  }

  @Override
  @Transactional(readOnly = true)
  public ParticipantInfoResponse getParticipantInfo(UUID participantId) {
    Participant participant = participantRepository.findById(participantId);
    return participantMapper.toParticipantInfoResponse(participant);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Participant> findAllByMeetingRoom(MeetingRoom meetingRoom) {
    return participantRepository.findAllByMeetingRoom(meetingRoom);
  }

  @Override
  @Transactional
  public void deleteParticipant(UUID participantId) {
    UUID authenticatedUserId = getAuthenticatedUserId();
    Participant participant = participantRepository.findById(participantId);

    // 비인증 상태이거나 본인이 아닌 경우 삭제 금지
    if (authenticatedUserId == null || !authenticatedUserId.equals(participant.getUserId())) {
      log.warn("참가자 삭제 권한 없음: participantId={}, requestUserId={}", participantId, authenticatedUserId);
      throw new CustomException(ErrorCode.PARTICIPANT_DELETE_FORBIDDEN);
    }
    participantRepository.deleteById(participantId);
  }

  // 로그인 사용자 방 참가 처리
  private ParticipantInfoResponse handleAuthenticatedUser(
      ParticipantLoginRequest request, MeetingRoom room, UUID userId) {
    // 이미 해당 방에 참가한 경우 기존 참가자 정보 반환
    Participant existing = participantRepository.findByMeetingRoomAndUserId(room, userId);
    if (existing != null) {
      return participantMapper.toParticipantInfoResponse(existing);
    }
    // username 미입력 시 User.nickname 기본값 사용
    String username = request.getUsername();
    if (CommonUtil.nvl(username, "").isBlank()) {
      User user = userRepository.findById(userId);
      username = user.getNickname();
    }
    Participant saved = participantRepository.save(Participant.createForUser(room, username, userId));
    return participantMapper.toParticipantInfoResponse(saved);
  }

  // Guest 방 참가 처리
  private ParticipantInfoResponse handleGuestUser(
      ParticipantLoginRequest request, MeetingRoom room) {
    if (CommonUtil.nvl(request.getUsername(), "").isBlank()) {
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }
    Participant participant = participantRepository.findByMeetingRoomAndUsername(room, request.getUsername());
    if (participant != null) {
      validatePassword(participant, request.getPassword());
      return participantMapper.toParticipantInfoResponse(participant);
    }
    Participant saved = participantRepository.save(
        Participant.create(room, request.getUsername(), request.getPassword()));
    return participantMapper.toParticipantInfoResponse(saved);
  }

  // SecurityContext에서 인증된 사용자 UUID 추출 (비인증 상태이면 null 반환)
  private UUID getAuthenticatedUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof UsernamePasswordAuthenticationToken
        && auth.getPrincipal() instanceof UUID userId) {
      return userId;
    }
    return null;
  }

  private void validatePassword(Participant participant, String password) {
    if (!CommonUtil.nvl(participant.getPassword(), "").isEmpty()) {
      if (password == null || !participant.getPassword().equals(password.trim())) {
        log.error("사용자: {}의 비밀번호가 일치하지 않습니다.", participant.getId());
        throw new CustomException(ErrorCode.INVALID_PASSWORD);
      }
    }
  }
}
