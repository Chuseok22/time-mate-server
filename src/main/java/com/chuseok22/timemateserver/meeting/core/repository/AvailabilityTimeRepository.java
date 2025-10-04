package com.chuseok22.timemateserver.meeting.core.repository;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.AvailabilityTime;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.util.List;

public interface AvailabilityTimeRepository {

  List<AvailabilityTime> saveAll(List<AvailabilityTime> availabilityTimes);

  void deleteAllByParticipant(Participant participant);
}
