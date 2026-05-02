package com.chuseok22.timemateserver.common.infrastructure.oauth2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

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

  @Test
  @DisplayName("name이 없으면 nickname은 빈 문자열 반환 (DB NOT NULL 제약 위반 방지)")
  void getNickname_nameNull_returnsEmpty() {
    // given
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("sub", "google-user-456");
    // name 없음

    // when
    GoogleOAuth2UserInfo info = new GoogleOAuth2UserInfo(attributes);

    // then
    assertThat(info.getNickname()).isEmpty();
  }

  @Test
  @DisplayName("sub가 없으면 OAuth2AuthenticationException 발생")
  void getProviderId_subNull_throwsException() {
    // given
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("name", "홍길동");
    // sub 없음

    // when
    GoogleOAuth2UserInfo info = new GoogleOAuth2UserInfo(attributes);

    // then
    assertThatThrownBy(info::getProviderId)
        .isInstanceOf(OAuth2AuthenticationException.class);
  }
}
