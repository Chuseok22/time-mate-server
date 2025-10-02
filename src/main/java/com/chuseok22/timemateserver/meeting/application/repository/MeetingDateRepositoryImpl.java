package com.chuseok22.timemateserver.meeting.application.repository;

import com.chuseok22.timemateserver.meeting.core.repository.MeetingDateRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.repository.MeetingDateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MeetingDateRepositoryImpl implements MeetingDateRepository {

  private final MeetingDateJpaRepository jpaRepository;

}
