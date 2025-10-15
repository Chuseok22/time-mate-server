package com.chuseok22.timemateserver.meeting.core.constant;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JoinCodeAlphabet {

  BASE58("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"),
  BASE32_CROCKFORD("0123456789ABCDEFGHJKMNPQRSTVWXYZ");

  private final String alphabet;

  private static final Pattern BASE58_PATTERN =
      Pattern.compile("^[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]{8}$");

  public static void validateBase58Pattern(String input) {
    if (!BASE58_PATTERN.matcher(input).matches()) {
      throw new CustomException(ErrorCode.BASE_58_JOIN_CODE_PATTERN_MISMATCH);
    }
  }
}
