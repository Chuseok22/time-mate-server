package com.chuseok22.timemateserver.meeting.application.repository;

import com.chuseok22.timemateserver.meeting.core.repository.AvailabilityTimeRepository;
import com.chuseok22.timemateserver.meeting.infrastructure.repository.AvailabilityTimeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AvailabilityTimeRepositoryImpl implements AvailabilityTimeRepository {

  private final AvailabilityTimeJpaRepository jpaRepository;

}
