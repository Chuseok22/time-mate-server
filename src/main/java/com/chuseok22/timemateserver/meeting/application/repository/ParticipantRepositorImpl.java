package com.chuseok22.timemateserver.meeting.application.repository;

import com.chuseok22.timemateserver.meeting.core.repository.ParticipantRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import com.chuseok22.timemateserver.meeting.infrastructure.repository.ParticipantJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ParticipantRepositorImpl implements ParticipantRepository {

  private final ParticipantJpaRepository jpaRepository;

  @Override
  public Participant save(Participant participant) {
    return jpaRepository.save(participant);
  }

  @Override
  public List<Participant> saveAll(List<Participant> participants) {
    return jpaRepository.saveAll(participants);
  }

  @Override
  public List<Participant> findAllByMeetingRoom(MeetingRoom meetingRoom) {
    return jpaRepository.findAllByMeetingRoom(meetingRoom);
  }

  @Override
  public boolean existsByMeetingRoomAndUsername(MeetingRoom meetingRoom, String username) {
    return jpaRepository.existsByMeetingRoomAndUsername(meetingRoom, username);
  }
}
