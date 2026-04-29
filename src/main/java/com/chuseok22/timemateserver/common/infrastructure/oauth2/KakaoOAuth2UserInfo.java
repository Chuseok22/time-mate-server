package com.chuseok22.timemateserver.common.infrastructure.oauth2;

import java.util.Map;

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
    return String.valueOf(attributes.get("id"));
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
