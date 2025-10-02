package com.chuseok22.timemateserver.meeting.core.service;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import java.time.LocalDate;
import java.util.List;

public interface MeetingDateService {

  List<MeetingDate> createDate(MeetingRoom room, List<LocalDate> dates);

  List<MeetingDate> getMeetingDates(MeetingRoom room);
}
