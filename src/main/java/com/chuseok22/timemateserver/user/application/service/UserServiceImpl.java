package com.chuseok22.timemateserver.user.application.service;

import com.chuseok22.timemateserver.meeting.core.repository.AvailabilityTimeRepository;
import com.chuseok22.timemateserver.meeting.core.repository.MeetingRoomRepository;
import com.chuseok22.timemateserver.meeting.core.repository.ParticipantRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import com.chuseok22.timemateserver.user.application.dto.response.UserInfoResponse;
import com.chuseok22.timemateserver.user.application.dto.response.UserRoomResponse;
import com.chuseok22.timemateserver.user.application.mapper.UserMapper;
import com.chuseok22.timemateserver.user.core.repository.UserRepository;
import com.chuseok22.timemateserver.user.core.service.UserService;
import com.chuseok22.timemateserver.user.infrastructure.entity.User;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final MeetingRoomRepository meetingRoomRepository;
  private final ParticipantRepository participantRepository;
  private final AvailabilityTimeRepository availabilityTimeRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public User findOrCreateUser(String provider, String providerId, String nickname, String email) {
    return userRepository.findByProviderAndProviderId(provider, providerId)
        .orElseGet(() -> {
          log.info("신규 소셜 사용자 생성: provider={}, providerId={}", provider, providerId);
          return userRepository.save(User.create(provider, providerId, nickname, email));
        });
  }

  @Override
  @Transactional(readOnly = true)
  public UserInfoResponse getUserInfo(UUID userId) {
    User user = userRepository.findById(userId);
    return userMapper.toUserInfoResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserRoomResponse> getUserRooms(UUID userId) {
    // 내가 생성한 방 목록
    List<MeetingRoom> createdRooms = meetingRoomRepository.findAllByCreatorUserId(userId);

    // 내가 참가자로 등록된 방 목록 (Participant.userId 기준)
    List<MeetingRoom> participatedRooms = participantRepository.findAllByUserId(userId)
        .stream()
        .map(Participant::getMeetingRoom)
        .toList();

    // 중복 제거: 내가 만든 방에 내가 참가자로도 등록된 경우 한 번만 포함
    Set<UUID> seen = createdRooms.stream().map(MeetingRoom::getId).collect(Collectors.toSet());

    List<MeetingRoom> allRooms = Stream.concat(
        createdRooms.stream(),
        participatedRooms.stream().filter(r -> seen.add(r.getId()))
    ).toList();

    return allRooms.stream()
        .map(room -> userMapper.toUserRoomResponse(room, userId))
        .toList();
  }

  @Override
  @Transactional
  public void deleteUser(UUID userId) {
    // 존재 여부 확인 (없으면 USER_NOT_FOUND)
    userRepository.findById(userId);

    // FK 순서: AvailabilityTime → Participant → User
    List<Participant> participants = participantRepository.findAllByUserId(userId);
    for (Participant participant : participants) {
      availabilityTimeRepository.deleteAllByParticipant(participant);
      participantRepository.deleteById(participant.getId());
    }

    log.info("회원탈퇴 처리: userId={}, 삭제된 참가자 수={}", userId, participants.size());
    userRepository.deleteById(userId);
  }
}
