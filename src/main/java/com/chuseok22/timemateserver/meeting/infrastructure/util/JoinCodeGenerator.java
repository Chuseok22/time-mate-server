package com.chuseok22.timemateserver.meeting.infrastructure.util;

import com.chuseok22.timemateserver.meeting.infrastructure.properties.JoinCodeProperties;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JoinCodeGenerator {

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

}
