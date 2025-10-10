package com.chuseok22.timemateserver.meeting.application.controller;

import com.chuseok22.timemateserver.common.application.aop.LogMonitoringInvocation;
import com.chuseok22.timemateserver.meeting.application.dto.request.UpsertAvailabilityRequest;
import com.chuseok22.timemateserver.meeting.core.service.AvailabilityTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/time")
@RequiredArgsConstructor
public class AvailabilityTimeController {

  private final AvailabilityTimeService availabilityTimeService;

  @PostMapping()
  @LogMonitoringInvocation
  public ResponseEntity<Void> setAvailabilityTime(
      @Valid @RequestBody UpsertAvailabilityRequest request
  ) {
    availabilityTimeService.setAvailabilityTime(request);
    return ResponseEntity.ok().build();
  }

}
