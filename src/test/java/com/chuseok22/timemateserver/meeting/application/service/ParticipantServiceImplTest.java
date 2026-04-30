package com.chuseok22.timemateserver.meeting.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.meeting.application.dto.request.ParticipantLoginRequest;
import com.chuseok22.timemateserver.meeting.application.dto.response.ParticipantInfoResponse;
import com.chuseok22.timemateserver.meeting.application.mapper.ParticipantMapper;
import com.chuseok22.timemateserver.meeting.core.repository.MeetingRoomRepository;
import com.chuseok22.timemateserver.meeting.core.repository.ParticipantRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import com.chuseok22.timemateserver.user.core.repository.UserRepository;
import com.chuseok22.timemateserver.user.infrastructure.entity.User;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceImplTest {

  @Mock
  private ParticipantRepository participantRepository;
  @Mock
  private MeetingRoomRepository meetingRoomRepository;
  @Mock
  private ParticipantMapper participantMapper;
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private ParticipantServiceImpl participantService;

  @BeforeEach
  void setUp() {
    // 각 테스트 전 SecurityContext 초기화하여 테스트 간 인증 상태 격리
    SecurityContextHolder.clearContext();
  }

  // ─────────────────────────────────────────────
  // loginParticipant — 게스트 사용자 테스트
  // ─────────────────────────────────────────────

  @Test
  @DisplayName("게스트 신규 사용자가 방에 참가하면 Participant 생성 후 반환")
  void loginParticipant_guestNewUser_createsParticipant() {
    // given
    UUID roomId = UUID.randomUUID();
    MeetingRoom mockRoom = MeetingRoom.builder().title("테스트 방").joinCode("TEST01").build();
    ParticipantLoginRequest request = new ParticipantLoginRequest(roomId, "홍길동", null);
    Participant saved = Participant.create(mockRoom, "홍길동", null);
    ParticipantInfoResponse expected = new ParticipantInfoResponse(UUID.randomUUID(), "홍길동");

    given(meetingRoomRepository.findById(roomId)).willReturn(mockRoom);
    given(participantRepository.findByMeetingRoomAndUsername(mockRoom, "홍길동")).willReturn(null);
    given(participantRepository.save(any(Participant.class))).willReturn(saved);
    given(participantMapper.toParticipantInfoResponse(saved)).willReturn(expected);

    // when
    ParticipantInfoResponse result = participantService.loginParticipant(request);

    // then
    assertThat(result.username()).isEqualTo("홍길동");
    verify(participantRepository).save(any(Participant.class));
  }

  @Test
  @DisplayName("게스트 사용자가 잘못된 비밀번호로 참가 시 INVALID_PASSWORD 예외 발생")
  void loginParticipant_guestWrongPassword_throwsException() {
    // given
    UUID roomId = UUID.randomUUID();
    MeetingRoom mockRoom = MeetingRoom.builder().title("테스트 방").joinCode("TEST01").build();
    ParticipantLoginRequest request = new ParticipantLoginRequest(roomId, "홍길동", "wrongPw");
    Participant existingWithPw = Participant.create(mockRoom, "홍길동", "correctPw");

    given(meetingRoomRepository.findById(roomId)).willReturn(mockRoom);
    given(participantRepository.findByMeetingRoomAndUsername(mockRoom, "홍길동"))
        .willReturn(existingWithPw);

    // when / then
    assertThatThrownBy(() -> participantService.loginParticipant(request))
        .isInstanceOf(CustomException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.INVALID_PASSWORD);
  }

  @Test
  @DisplayName("게스트 사용자가 username 없이 요청 시 INVALID_REQUEST 예외 발생")
  void loginParticipant_guestBlankUsername_throwsInvalidRequest() {
    // given
    UUID roomId = UUID.randomUUID();
    MeetingRoom mockRoom = MeetingRoom.builder().title("테스트 방").joinCode("TEST01").build();
    ParticipantLoginRequest request = new ParticipantLoginRequest(roomId, null, null);

    given(meetingRoomRepository.findById(roomId)).willReturn(mockRoom);

    // when / then
    assertThatThrownBy(() -> participantService.loginParticipant(request))
        .isInstanceOf(CustomException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.INVALID_REQUEST);
  }

  // ─────────────────────────────────────────────
  // loginParticipant — 로그인 사용자 테스트
  // ─────────────────────────────────────────────

  @Test
  @DisplayName("로그인 사용자가 방에 참가하면 userId가 설정된 Participant 생성 후 반환")
  void loginParticipant_authenticatedUser_createsWithUserId() {
    // given
    UUID userId = UUID.randomUUID();
    UUID roomId = UUID.randomUUID();
    // SecurityContext에 인증 정보 설정
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);

    MeetingRoom mockRoom = MeetingRoom.builder().title("테스트 방").joinCode("TEST01").build();
    User mockUser = User.create("google", "google-123", "구글닉네임", "test@gmail.com");
    ParticipantLoginRequest request = new ParticipantLoginRequest(roomId, null, null);
    Participant saved = Participant.createForUser(mockRoom, "구글닉네임", userId);
    ParticipantInfoResponse expected = new ParticipantInfoResponse(UUID.randomUUID(), "구글닉네임");

    given(meetingRoomRepository.findById(roomId)).willReturn(mockRoom);
    // 이미 참가한 기록 없음
    given(participantRepository.findByMeetingRoomAndUserId(mockRoom, userId)).willReturn(null);
    // username이 null이므로 User 닉네임 조회
    given(userRepository.findById(userId)).willReturn(mockUser);
    given(participantRepository.save(any(Participant.class))).willReturn(saved);
    given(participantMapper.toParticipantInfoResponse(saved)).willReturn(expected);

    // when
    ParticipantInfoResponse result = participantService.loginParticipant(request);

    // then
    assertThat(result.username()).isEqualTo("구글닉네임");
    verify(participantRepository).save(any(Participant.class));
    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("로그인 사용자가 이미 참가한 방에 재요청 시 기존 참가자 정보 반환")
  void loginParticipant_authenticatedUserAlreadyJoined_returnsExisting() {
    // given
    UUID userId = UUID.randomUUID();
    UUID roomId = UUID.randomUUID();
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);

    MeetingRoom mockRoom = MeetingRoom.builder().title("테스트 방").joinCode("TEST01").build();
    ParticipantLoginRequest request = new ParticipantLoginRequest(roomId, null, null);
    Participant existing = Participant.createForUser(mockRoom, "기존닉네임", userId);
    ParticipantInfoResponse expected = new ParticipantInfoResponse(UUID.randomUUID(), "기존닉네임");

    given(meetingRoomRepository.findById(roomId)).willReturn(mockRoom);
    // 이미 참가한 기록 있음
    given(participantRepository.findByMeetingRoomAndUserId(mockRoom, userId)).willReturn(existing);
    given(participantMapper.toParticipantInfoResponse(existing)).willReturn(expected);

    // when
    ParticipantInfoResponse result = participantService.loginParticipant(request);

    // then
    assertThat(result.username()).isEqualTo("기존닉네임");
    // 이미 참가 중이므로 save 호출 없어야 함
    verify(participantRepository, org.mockito.Mockito.never()).save(any(Participant.class));
  }
}
