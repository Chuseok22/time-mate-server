package com.chuseok22.timemateserver.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.meeting.core.repository.MeetingRoomRepository;
import com.chuseok22.timemateserver.meeting.core.repository.ParticipantRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import com.chuseok22.timemateserver.user.application.dto.response.UserInfoResponse;
import com.chuseok22.timemateserver.user.application.dto.response.UserRoomResponse;
import com.chuseok22.timemateserver.user.application.mapper.UserMapper;
import com.chuseok22.timemateserver.user.core.repository.UserRepository;
import com.chuseok22.timemateserver.user.infrastructure.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private MeetingRoomRepository meetingRoomRepository;
  @Mock
  private ParticipantRepository participantRepository;
  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  // ─────────────────────────────────────────────
  // findOrCreateUser 테스트
  // ─────────────────────────────────────────────

  @Test
  @DisplayName("신규 소셜 사용자 최초 로그인 시 User 생성 후 반환")
  void findOrCreateUser_newUser_createsAndReturns() {
    // given
    given(userRepository.findByProviderAndProviderId("google", "google-123"))
        .willReturn(Optional.empty());
    User savedUser = User.create("google", "google-123", "홍길동", "test@gmail.com");
    given(userRepository.save(any(User.class))).willReturn(savedUser);

    // when
    User result = userService.findOrCreateUser("google", "google-123", "홍길동", "test@gmail.com");

    // then
    assertThat(result.getProvider()).isEqualTo("google");
    assertThat(result.getNickname()).isEqualTo("홍길동");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("기존 소셜 사용자 재로그인 시 기존 User 반환하고 save 호출하지 않음")
  void findOrCreateUser_existingUser_returnsExistingWithoutSave() {
    // given
    User existing = User.create("google", "google-123", "홍길동", "test@gmail.com");
    given(userRepository.findByProviderAndProviderId("google", "google-123"))
        .willReturn(Optional.of(existing));

    // when
    User result = userService.findOrCreateUser("google", "google-123", "홍길동", "test@gmail.com");

    // then
    assertThat(result).isEqualTo(existing);
    verify(userRepository, never()).save(any(User.class));
  }

  // ─────────────────────────────────────────────
  // getUserInfo 테스트
  // ─────────────────────────────────────────────

  @Test
  @DisplayName("유효한 userId로 내 정보 조회 시 UserInfoResponse 반환")
  void getUserInfo_validUserId_returnsUserInfoResponse() {
    // given
    UUID userId = UUID.randomUUID();
    User user = User.create("google", "google-123", "홍길동", "test@gmail.com");
    UserInfoResponse expected = new UserInfoResponse(userId, "홍길동", "test@gmail.com");
    given(userRepository.findById(userId)).willReturn(user);
    given(userMapper.toUserInfoResponse(user)).willReturn(expected);

    // when
    UserInfoResponse result = userService.getUserInfo(userId);

    // then
    assertThat(result.nickname()).isEqualTo("홍길동");
    assertThat(result.email()).isEqualTo("test@gmail.com");
  }

  @Test
  @DisplayName("존재하지 않는 userId로 내 정보 조회 시 USER_NOT_FOUND 예외 발생")
  void getUserInfo_notFound_throwsCustomException() {
    // given
    UUID userId = UUID.randomUUID();
    given(userRepository.findById(userId))
        .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

    // when / then
    // CustomException의 errorCode 필드까지 검증하여 정확한 에러 종류 확인
    assertThatThrownBy(() -> userService.getUserInfo(userId))
        .isInstanceOf(CustomException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.USER_NOT_FOUND);
  }

  // ─────────────────────────────────────────────
  // getUserRooms 테스트
  // ─────────────────────────────────────────────

  @Test
  @DisplayName("만든 방도 참가한 방도 없을 때 빈 리스트 반환")
  void getUserRooms_noRooms_returnsEmptyList() {
    // given
    UUID userId = UUID.randomUUID();
    given(meetingRoomRepository.findAllByCreatorUserId(userId)).willReturn(List.of());
    given(participantRepository.findAllByUserId(userId)).willReturn(List.of());

    // when
    List<UserRoomResponse> result = userService.getUserRooms(userId);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("내가 만든 방만 있을 때 해당 방을 방장으로 반환")
  void getUserRooms_onlyCreatedRooms_returnsAsOwner() {
    // given
    UUID userId = UUID.randomUUID();
    MeetingRoom createdRoom = MeetingRoom.builder()
        .title("내가 만든 방")
        .joinCode("ABC123")
        .creatorUserId(userId)
        .build();
    UserRoomResponse expectedResponse = new UserRoomResponse(
        UUID.randomUUID(), "내가 만든 방", "ABC123", true);

    given(meetingRoomRepository.findAllByCreatorUserId(userId)).willReturn(List.of(createdRoom));
    given(participantRepository.findAllByUserId(userId)).willReturn(List.of());
    given(userMapper.toUserRoomResponse(createdRoom, userId)).willReturn(expectedResponse);

    // when
    List<UserRoomResponse> result = userService.getUserRooms(userId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).isOwner()).isTrue();
    assertThat(result.get(0).title()).isEqualTo("내가 만든 방");
  }

  @Test
  @DisplayName("내가 참가한 방만 있을 때 해당 방을 참가자로 반환")
  void getUserRooms_onlyParticipatedRooms_returnsAsParticipant() {
    // given
    UUID userId = UUID.randomUUID();
    MeetingRoom participatedRoom = MeetingRoom.builder()
        .title("내가 참가한 방")
        .joinCode("DEF456")
        .creatorUserId(UUID.randomUUID())
        .build();
    Participant participant = Participant.builder()
        .meetingRoom(participatedRoom)
        .username("홍길동")
        .userId(userId)
        .build();
    UserRoomResponse expectedResponse = new UserRoomResponse(
        UUID.randomUUID(), "내가 참가한 방", "DEF456", false);

    given(meetingRoomRepository.findAllByCreatorUserId(userId)).willReturn(List.of());
    given(participantRepository.findAllByUserId(userId)).willReturn(List.of(participant));
    given(userMapper.toUserRoomResponse(participatedRoom, userId)).willReturn(expectedResponse);

    // when
    List<UserRoomResponse> result = userService.getUserRooms(userId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).isOwner()).isFalse();
    assertThat(result.get(0).title()).isEqualTo("내가 참가한 방");
  }

  @Test
  @DisplayName("내가 만든 방과 참가한 방이 동일한 경우 중복 제거하여 1건만 반환")
  void getUserRooms_sameRoomCreatedAndParticipated_deduplicates() {
    // given
    UUID userId = UUID.randomUUID();
    // 고정 UUID를 주입하여 ID 기반 중복 제거를 명확히 검증
    UUID roomId = UUID.randomUUID();
    MeetingRoom room = mock(MeetingRoom.class);
    given(room.getId()).willReturn(roomId);

    Participant participant = Participant.builder()
        .meetingRoom(room)
        .username("홍길동")
        .userId(userId)
        .build();
    UserRoomResponse expectedResponse = new UserRoomResponse(
        roomId, "내가 만들고 참가한 방", "SAME01", true);

    given(meetingRoomRepository.findAllByCreatorUserId(userId)).willReturn(List.of(room));
    given(participantRepository.findAllByUserId(userId)).willReturn(List.of(participant));
    // 동일 roomId를 가진 방이므로 mapper 호출은 1번만 발생
    given(userMapper.toUserRoomResponse(room, userId)).willReturn(expectedResponse);

    // when
    List<UserRoomResponse> result = userService.getUserRooms(userId);

    // then
    // 동일 UUID를 가진 방은 seen Set에서 중복 제거되어 1건만 반환되어야 함
    assertThat(result).hasSize(1);
    verify(userMapper).toUserRoomResponse(room, userId);
  }
}
