package com.chuseok22.timemateserver.meeting.core.repository;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import java.util.List;

public interface MeetingDateRepository {

  MeetingDate save(MeetingDate meetingDate);

  List<MeetingDate> saveAll(List<MeetingDate> meetingDates);

  List<MeetingDate> findAllByMeetingRoom(MeetingRoom room);

}
