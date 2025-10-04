package com.chuseok22.timemateserver.meeting.application.repository;

import com.chuseok22.timemateserver.meeting.core.repository.AvailabilityTimeRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.AvailabilityTime;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import com.chuseok22.timemateserver.meeting.infrastructure.repository.AvailabilityTimeJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AvailabilityTimeRepositoryImpl implements AvailabilityTimeRepository {

  private final AvailabilityTimeJpaRepository jpaRepository;

  @Override
  public List<AvailabilityTime> saveAll(List<AvailabilityTime> availabilityTimes) {
    return jpaRepository.saveAll(availabilityTimes);
  }

  @Override
  public void deleteAllByParticipant(Participant participant) {
    jpaRepository.deleteAllByParticipant(participant);
  }
}
