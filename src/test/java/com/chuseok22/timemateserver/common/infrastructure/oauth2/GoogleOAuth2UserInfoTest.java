package com.chuseok22.timemateserver.common.infrastructure.oauth2;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GoogleOAuth2UserInfoTest {

  @Test
  @DisplayName("Google 속성에서 provider, providerId, nickname, email 정상 추출")
  void getAttributes_success() {
    // given
    Map<String, Object> attributes = Map.of(
        "sub", "google-user-123",
        "name", "홍길동",
        "email", "hong@gmail.com"
    );

    // when
    GoogleOAuth2UserInfo info = new GoogleOAuth2UserInfo(attributes);

    // then
    assertThat(info.getProvider()).isEqualTo("google");
    assertThat(info.getProviderId()).isEqualTo("google-user-123");
    assertThat(info.getNickname()).isEqualTo("홍길동");
    assertThat(info.getEmail()).isEqualTo("hong@gmail.com");
  }
}
