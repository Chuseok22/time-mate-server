package com.chuseok22.timemateserver.meeting.application.service;

import com.chuseok22.timemateserver.meeting.application.dto.request.AvailabilityTimeRequest;
import com.chuseok22.timemateserver.meeting.application.dto.request.UpsertAvailabilityRequest;
import com.chuseok22.timemateserver.meeting.core.repository.AvailabilityTimeRepository;
import com.chuseok22.timemateserver.meeting.core.repository.MeetingDateRepository;
import com.chuseok22.timemateserver.meeting.core.repository.ParticipantRepository;
import com.chuseok22.timemateserver.meeting.core.service.AvailabilityTimeService;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.AvailabilityTime;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvailabilityTimeServiceImpl implements AvailabilityTimeService {

  private final AvailabilityTimeRepository availabilityTimeRepository;
  private final MeetingDateRepository meetingDateRepository;
  private final ParticipantRepository participantRepository;
  private final EntityManager em;

  @Override
  @Transactional
  public void setAvailabilityTime(UpsertAvailabilityRequest request) {
    Participant participant = participantRepository.findById(request.getParticipantId());
    deleteAllByParticipant(participant);
    em.flush();
    request.getAvailabilityTimeRequests()
        .forEach(req -> saveAvailabilityTimes(participant, req));
  }

  private void deleteAllByParticipant(Participant participant) {
    availabilityTimeRepository.deleteAllByParticipant(participant);
  }

  private void saveAvailabilityTimes(Participant participant, AvailabilityTimeRequest request) {
    MeetingRoom room = participant.getMeetingRoom();
    MeetingDate meetingDate = meetingDateRepository.findOptionalByMeetingRoomAndDate(room, request.getDate());
    List<AvailabilityTime> availabilityTimes = request.getTimeSlots().stream()
        .map(timeslot -> AvailabilityTime.create(participant, meetingDate, timeslot))
        .collect(Collectors.toList());
    availabilityTimeRepository.saveAll(availabilityTimes);
  }
}
