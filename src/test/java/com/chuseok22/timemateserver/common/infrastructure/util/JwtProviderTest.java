package com.chuseok22.timemateserver.common.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.infrastructure.properties.JwtProperties;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

  private JwtProvider jwtProvider;
  // 32바이트 이상의 Base64 인코딩된 시크릿 (테스트 전용)
  private static final String TEST_SECRET =
      "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RpbmctbG9uZ2Vub3VnaA==";

  @BeforeEach
  void setUp() {
    JwtProperties properties = new JwtProperties(TEST_SECRET, 3600000L);
    jwtProvider = new JwtProvider(properties);
  }

  @Test
  @DisplayName("userId로 토큰 생성 후 동일한 userId 추출 성공")
  void createAndParseToken_success() {
    UUID userId = UUID.randomUUID();

    String token = jwtProvider.createToken(userId);
    UUID parsed = jwtProvider.getUserId(token);

    assertThat(parsed).isEqualTo(userId);
  }

  @Test
  @DisplayName("만료된 토큰 검증 시 TOKEN_EXPIRED CustomException 발생")
  void expiredToken_throwsTokenExpired() {
    JwtProperties shortLived = new JwtProperties(TEST_SECRET, -1L);
    JwtProvider shortProvider = new JwtProvider(shortLived);
    String token = shortProvider.createToken(UUID.randomUUID());

    assertThatThrownBy(() -> shortProvider.getUserId(token))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("만료");
  }

  @Test
  @DisplayName("잘못된 토큰 검증 시 INVALID_TOKEN CustomException 발생")
  void invalidToken_throwsInvalidToken() {
    assertThatThrownBy(() -> jwtProvider.getUserId("invalid.token.value"))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("유효하지 않은");
  }
}
