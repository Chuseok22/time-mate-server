package com.chuseok22.timemateserver.meeting.infrastructure.repository;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRoomJpaRepository extends JpaRepository<MeetingRoom, UUID> {

}
