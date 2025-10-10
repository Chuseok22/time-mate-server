package com.chuseok22.timemateserver.meeting.core.repository;

import com.chuseok22.timemateserver.meeting.core.constant.TimeSlot;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.AvailabilityTime;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.Participant;
import java.util.List;
import java.util.Map;

public interface AvailabilityTimeRepository {

  List<AvailabilityTime> saveAll(List<AvailabilityTime> availabilityTimes);

  void deleteAllByParticipant(Participant participant);

  List<AvailabilityTime> findAllByMeetingDate(MeetingDate meetingDate);

  Map<TimeSlot, List<Participant>> findParticipantsByMeetingDateGroupWithTimeSlot(MeetingDate meetingDate);
}
