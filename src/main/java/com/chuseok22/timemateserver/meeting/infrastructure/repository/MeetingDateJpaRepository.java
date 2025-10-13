package com.chuseok22.timemateserver.meeting.infrastructure.repository;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingDateJpaRepository extends JpaRepository<MeetingDate, UUID> {

  List<MeetingDate> findAllByMeetingRoom(MeetingRoom room);

  Optional<MeetingDate> findByMeetingRoomAndDate(MeetingRoom meetingRoom, LocalDate date);
}
