package com.chuseok22.timemateserver.meeting.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JoinCodeAlphabet {

  BASE58("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"),
  BASE32_CROCKFORD("0123456789ABCDEFGHJKMNPQRSTVWXYZ");

  private final String alphabet;
}
