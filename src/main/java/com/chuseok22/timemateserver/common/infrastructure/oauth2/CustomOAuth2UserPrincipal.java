package com.chuseok22.timemateserver.common.infrastructure.oauth2;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

// 성공 핸들러에서 JWT 발급 시 userId 참조를 위한 커스텀 OAuth2User
public class CustomOAuth2UserPrincipal implements OAuth2User {

  private final UUID userId;
  private final Map<String, Object> attributes;

  public CustomOAuth2UserPrincipal(UUID userId, Map<String, Object> attributes) {
    this.userId = userId;
    this.attributes = attributes;
  }

  public UUID getUserId() {
    return userId;
  }

  @Override
  public Map<String, Object> getAttributes() {
    // 외부에서 내부 상태를 변경할 수 없도록 방어 복사 반환
    return Map.copyOf(attributes);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getName() {
    return userId.toString();
  }
}
