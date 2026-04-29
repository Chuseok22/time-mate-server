package com.chuseok22.timemateserver.meeting.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantLoginRequest {

  @NotNull(message = "방 id를 입력하세요")
  private UUID meetingRoomId;

  // 로그인 사용자는 null 허용 (서비스에서 닉네임 기본값 처리)
  private String username;

  private String password;
}
