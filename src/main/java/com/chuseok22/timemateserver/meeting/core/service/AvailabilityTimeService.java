package com.chuseok22.timemateserver.meeting.core.service;

import com.chuseok22.timemateserver.meeting.application.dto.request.UpsertAvailabilityRequest;

public interface AvailabilityTimeService {

  void setAvailabilityTime(UpsertAvailabilityRequest request);
}
