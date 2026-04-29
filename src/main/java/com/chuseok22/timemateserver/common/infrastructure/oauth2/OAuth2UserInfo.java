package com.chuseok22.timemateserver.common.infrastructure.oauth2;

public interface OAuth2UserInfo {

  String getProvider();    // "google" 또는 "kakao"

  String getProviderId();  // 소셜 제공자의 고유 식별자

  String getNickname();

  String getEmail();
}
