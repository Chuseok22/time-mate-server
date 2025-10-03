package com.chuseok22.timemateserver.meeting.application.controller;

import com.chuseok22.timemateserver.common.application.aop.LogMonitoringInvocation;
import com.chuseok22.timemateserver.meeting.application.dto.request.CreateParticipantRequest;
import com.chuseok22.timemateserver.meeting.core.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/participant")
@RequiredArgsConstructor
public class ParticipantController {

  private final ParticipantService participantService;

  @PostMapping("")
  @LogMonitoringInvocation
  public ResponseEntity<Void> createParticipant(
      @Valid @RequestBody CreateParticipantRequest request
  ) {
    participantService.createParticipant(request);
    return ResponseEntity.ok().build();
  }

}
