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

  // Meeting Room

  MEETING_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Meeting Room을 찾을 수 없습니다."),

  JOIN_CODE_DUPLICATE(HttpStatus.INTERNAL_SERVER_ERROR, "중복된 방 참가 코드입니다."),

  BASE_58_JOIN_CODE_PATTERN_MISMATCH(HttpStatus.BAD_REQUEST, "방 참가 코드 형식이 잘못되었습니다."),

  // Meeting Date

  MEETING_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "Meeting Date를 찾을 수 없습니다."),

  // TimeSlot

  TIME_SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "LocalTime에 맞는 TimeSlot이 없습니다."),

  // Participant

  PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "참가자를 찾을 수 없습니다."),

  DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 이름입니다."),

  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

  // Telegram

  TELEGRAM_MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "텔레그램 메시지 발송 실패"),
  ;

  private final HttpStatus status;
  private final String message;
}
