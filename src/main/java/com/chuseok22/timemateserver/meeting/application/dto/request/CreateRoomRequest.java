package com.chuseok22.timemateserver.meeting.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {

  @NotBlank(message = "방 제목을 입력하세요")
  private String title;

  @NotEmpty(message = "후보 날짜를 입력하세요")
  private List<LocalDate> dates;
}
