package com.chuseok22.timemateserver.meeting.core.constant;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeSlot {
  // 오전 8시부터 자정까지 30분 간격
  SLOT_08_00("08:00", LocalTime.of(8, 0)),
  SLOT_08_30("08:30", LocalTime.of(8, 30)),
  SLOT_09_00("09:00", LocalTime.of(9, 0)),
  SLOT_09_30("09:30", LocalTime.of(9, 30)),
  SLOT_10_00("10:00", LocalTime.of(10, 0)),
  SLOT_10_30("10:30", LocalTime.of(10, 30)),
  SLOT_11_00("11:00", LocalTime.of(11, 0)),
  SLOT_11_30("11:30", LocalTime.of(11, 30)),
  SLOT_12_00("12:00", LocalTime.of(12, 0)),
  SLOT_12_30("12:30", LocalTime.of(12, 30)),
  SLOT_13_00("13:00", LocalTime.of(13, 0)),
  SLOT_13_30("13:30", LocalTime.of(13, 30)),
  SLOT_14_00("14:00", LocalTime.of(14, 0)),
  SLOT_14_30("14:30", LocalTime.of(14, 30)),
  SLOT_15_00("15:00", LocalTime.of(15, 0)),
  SLOT_15_30("15:30", LocalTime.of(15, 30)),
  SLOT_16_00("16:00", LocalTime.of(16, 0)),
  SLOT_16_30("16:30", LocalTime.of(16, 30)),
  SLOT_17_00("17:00", LocalTime.of(17, 0)),
  SLOT_17_30("17:30", LocalTime.of(17, 30)),
  SLOT_18_00("18:00", LocalTime.of(18, 0)),
  SLOT_18_30("18:30", LocalTime.of(18, 30)),
  SLOT_19_00("19:00", LocalTime.of(19, 0)),
  SLOT_19_30("19:30", LocalTime.of(19, 30)),
  SLOT_20_00("20:00", LocalTime.of(20, 0)),
  SLOT_20_30("20:30", LocalTime.of(20, 30)),
  SLOT_21_00("21:00", LocalTime.of(21, 0)),
  SLOT_21_30("21:30", LocalTime.of(21, 30)),
  SLOT_22_00("22:00", LocalTime.of(22, 0)),
  SLOT_22_30("22:30", LocalTime.of(22, 30)),
  SLOT_23_00("23:00", LocalTime.of(23, 0)),
  SLOT_23_30("23:30", LocalTime.of(23, 30));


  private final String displayName;
  private final LocalTime startTime;

  // LocalTime으로 TimeSlot 찾기
  public static TimeSlot fromTime(LocalTime time) {
    for (TimeSlot slot : values()) {
      if (slot.startTime.equals(time)) {
        return slot;
      }
    }
    throw new CustomException(ErrorCode.TIME_SLOT_NOT_FOUND);
  }
}
