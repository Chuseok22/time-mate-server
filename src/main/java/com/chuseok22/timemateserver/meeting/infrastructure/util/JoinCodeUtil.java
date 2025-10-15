package com.chuseok22.timemateserver.meeting.infrastructure.util;

import static com.chuseok22.timemateserver.meeting.core.constant.JoinCodeAlphabet.BASE58;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.meeting.infrastructure.properties.JoinCodeProperties;
import java.security.SecureRandom;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JoinCodeUtil {

  private final JoinCodeProperties properties;
  private final SecureRandom random = new SecureRandom();

  public String generate() {
    String alphabet = properties.alphabet().getAlphabet();
    int length = properties.length();

    char[] c = new char[length];
    for (int i = 0; i < length; i++) {
      c[i] = alphabet.charAt(random.nextInt(alphabet.length()));
    }
    return new String(c);
  }

  public void validateBase58Pattern(String joinCode) {
    String regex =  "^[%s]{%d}$".formatted(BASE58.getAlphabet(), properties.length());
    if (!Pattern.compile(regex).matcher(joinCode).matches()) {
      throw new CustomException(ErrorCode.BASE_58_JOIN_CODE_PATTERN_MISMATCH);
    }
  }
}
