package com.chuseok22.timemateserver.common.infrastructure.oauth2;

import java.util.Map;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

  private final Map<String, Object> attributes;

  public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String getProvider() {
    return "google";
  }

  @Override
  public String getProviderId() {
    Object sub = attributes.get("sub");
    if (sub == null) {
      throw new OAuth2AuthenticationException(
          new OAuth2Error(
              "missing_provider_id",
              "Google 사용자 ID를 찾을 수 없습니다",
              null
          )
      );
    }
    return (String) sub;
  }

  @Override
  public String getNickname() {
    String name = (String) attributes.get("name");
    return name != null ? name : "";
  }

  @Override
  public String getEmail() {
    return (String) attributes.get("email");
  }
}
