package com.chuseok22.timemateserver.common.infrastructure.oauth2;

import static org.assertj.core.api.Assertions.assertThat;

import com.chuseok22.timemateserver.user.core.service.UserService;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private CustomOAuth2UserService customOAuth2UserService;

  @Test
  @DisplayName("CustomOAuth2UserService 빈 생성 및 UserService 의존성 주입 확인")
  void beanCreation_success() {
    // CustomOAuth2UserService는 super.loadUser()가 HTTP 호출을 포함하므로
    // 단위 테스트에서는 빈 생성 및 의존성 주입 여부만 검증
    assertThat(customOAuth2UserService).isNotNull();
  }

  @Test
  @DisplayName("Google OAuth2UserInfo: provider=google, attributes 매핑 정상 동작")
  void googleUserInfo_mapping_success() {
    // given
    Map<String, Object> attrs = Map.of(
        "sub", "g-123",
        "name", "구글유저",
        "email", "g@google.com"
    );

    // when
    GoogleOAuth2UserInfo info = new GoogleOAuth2UserInfo(attrs);

    // then
    assertThat(info.getProvider()).isEqualTo("google");
    assertThat(info.getProviderId()).isEqualTo("g-123");
    assertThat(info.getNickname()).isEqualTo("구글유저");
    assertThat(info.getEmail()).isEqualTo("g@google.com");
  }

  @Test
  @DisplayName("CustomOAuth2UserPrincipal: getUserId()로 userId 반환, getName()은 userId 문자열")
  void customPrincipal_userId_getName() {
    // given
    UUID userId = UUID.randomUUID();
    Map<String, Object> attrs = Map.of("sub", "test");

    // when
    CustomOAuth2UserPrincipal principal = new CustomOAuth2UserPrincipal(userId, attrs);

    // then
    assertThat(principal.getUserId()).isEqualTo(userId);
    assertThat(principal.getName()).isEqualTo(userId.toString());
    assertThat(principal.getAuthorities())
        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
  }
}
