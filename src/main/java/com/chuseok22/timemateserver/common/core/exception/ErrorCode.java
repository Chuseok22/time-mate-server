package com.chuseok22.timemateserver.common.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // GLOBAL

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),

  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),


  // TimeSlot

  TIME_SLOT_NOT_FOUND(HttpStatus.BAD_REQUEST, "LocalTime에 맞는 TimeSlot이 없습니다."),
  ;

  private final HttpStatus status;
  private final String message;
}
