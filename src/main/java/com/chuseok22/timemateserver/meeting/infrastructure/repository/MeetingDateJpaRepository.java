package com.chuseok22.timemateserver.meeting.infrastructure.repository;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingDate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingDateJpaRepository extends JpaRepository<MeetingDate, UUID> {

}
