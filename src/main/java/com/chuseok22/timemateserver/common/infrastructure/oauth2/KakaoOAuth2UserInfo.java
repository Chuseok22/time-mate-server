package com.chuseok22.timemateserver.common.infrastructure.oauth2;

import java.util.Map;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

  private final Map<String, Object> attributes;

  public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String getProvider() {
    return "kakao";
  }

  @Override
  public String getProviderId() {
    Object id = attributes.get("id");
    // 카카오 응답에 사용자 ID가 없는 경우 OAuth2 인증 예외로 처리
    if (id == null) {
      throw new OAuth2AuthenticationException(
          new OAuth2Error(
              "missing_provider_id",
              "카카오 사용자 ID를 찾을 수 없습니다",
              null
          )
      );
    }
    return String.valueOf(id);
  }

  @SuppressWarnings("unchecked")
  @Override
  public String getNickname() {
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    if (kakaoAccount == null) {
      return "";
    }
    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
    return profile != null ? (String) profile.get("nickname") : "";
  }

  @SuppressWarnings("unchecked")
  @Override
  public String getEmail() {
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
  }
}
