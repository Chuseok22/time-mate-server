package com.chuseok22.timemateserver.meeting.application.controller;

import com.chuseok22.timemateserver.common.application.aop.LogMonitoringInvocation;
import com.chuseok22.timemateserver.meeting.application.dto.request.ParticipantLoginRequest;
import com.chuseok22.timemateserver.meeting.application.dto.response.ParticipantInfoResponse;
import com.chuseok22.timemateserver.meeting.core.service.ParticipantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/participant")
@RequiredArgsConstructor
public class ParticipantController implements ParticipantControllerDocs {

  private final ParticipantService participantService;

  @LogMonitoringInvocation
  @PostMapping("")
  public ResponseEntity<ParticipantInfoResponse> createParticipant(
      @Valid @RequestBody ParticipantLoginRequest request
  ) {
    return ResponseEntity.ok(participantService.loginParticipant(request));
  }

  @LogMonitoringInvocation
  @GetMapping("{participant-id}")
  public ResponseEntity<ParticipantInfoResponse> getParticipant(
      @NotNull @PathVariable(name = "participant-id") UUID participantId
  ) {
    return ResponseEntity.ok(participantService.getParticipantInfo(participantId));
  }

  @LogMonitoringInvocation
  @DeleteMapping("/{participant-id}")
  public ResponseEntity<Void> deleteParticipant(
      @PathVariable(name = "participant-id") UUID participantId) {
    participantService.deleteParticipant(participantId);
    return ResponseEntity.noContent().build();
  }
}
