package com.chuseok22.timemateserver.meeting.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateParticipantRequest {

  @NotNull(message = "방 id를 입력하세요")
  private UUID meetingRoomId;

  @NotBlank(message = "이름을 입력하세요")
  private String username;

  private String password;
}
