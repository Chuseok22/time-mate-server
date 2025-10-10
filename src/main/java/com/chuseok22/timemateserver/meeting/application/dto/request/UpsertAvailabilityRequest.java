package com.chuseok22.timemateserver.meeting.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpsertAvailabilityRequest {

  @NotNull(message = "참가자 id를 입력하세요")
  private UUID participantId;

  @Valid
  private List<AvailabilityTimeRequest> availabilityTimeRequests;
}
