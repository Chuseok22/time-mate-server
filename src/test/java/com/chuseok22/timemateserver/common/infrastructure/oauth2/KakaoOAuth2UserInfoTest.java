package com.chuseok22.timemateserver.common.infrastructure.oauth2;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KakaoOAuth2UserInfoTest {

  @Test
  @DisplayName("Kakao 속성에서 provider, providerId, nickname, email 정상 추출")
  void getAttributes_success() {
    // given
    Map<String, Object> profile = new HashMap<>();
    profile.put("nickname", "카카오유저");

    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("profile", profile);
    kakaoAccount.put("email", "kakao@kakao.com");

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("id", 987654321L);
    attributes.put("kakao_account", kakaoAccount);

    // when
    KakaoOAuth2UserInfo info = new KakaoOAuth2UserInfo(attributes);

    // then
    assertThat(info.getProvider()).isEqualTo("kakao");
    assertThat(info.getProviderId()).isEqualTo("987654321");
    assertThat(info.getNickname()).isEqualTo("카카오유저");
    assertThat(info.getEmail()).isEqualTo("kakao@kakao.com");
  }

  @Test
  @DisplayName("kakao_account가 null이면 nickname은 빈 문자열, email은 null 반환")
  void getAttributes_kakaoAccountNull_returnsDefaults() {
    // given
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("id", 111L);
    // kakao_account 없음

    // when
    KakaoOAuth2UserInfo info = new KakaoOAuth2UserInfo(attributes);

    // then
    assertThat(info.getNickname()).isEmpty();
    assertThat(info.getEmail()).isNull();
  }

  @Test
  @DisplayName("profile이 null이면 nickname은 빈 문자열 반환")
  void getNickname_profileNull_returnsEmpty() {
    // given
    Map<String, Object> kakaoAccount = new HashMap<>();
    // profile 없음
    kakaoAccount.put("email", "test@kakao.com");

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("id", 222L);
    attributes.put("kakao_account", kakaoAccount);

    // when
    KakaoOAuth2UserInfo info = new KakaoOAuth2UserInfo(attributes);

    // then
    assertThat(info.getNickname()).isEmpty();
  }
}
