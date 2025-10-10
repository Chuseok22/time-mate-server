package com.chuseok22.timemateserver.meeting.application.dto.request;

import com.chuseok22.timemateserver.meeting.core.constant.TimeSlot;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityTimeRequest {

  @NotNull
  private LocalDate date;

  @Size(max = 32)
  private List<TimeSlot> timeSlots;
}
