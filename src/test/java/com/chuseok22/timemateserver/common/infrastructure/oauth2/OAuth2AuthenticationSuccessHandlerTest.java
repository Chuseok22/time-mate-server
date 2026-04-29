package com.chuseok22.timemateserver.common.infrastructure.oauth2;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.chuseok22.timemateserver.common.infrastructure.properties.AppProperties;
import com.chuseok22.timemateserver.common.infrastructure.util.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private AppProperties appProperties;

  @InjectMocks
  private OAuth2AuthenticationSuccessHandler successHandler;

  private UUID userId;
  private CustomOAuth2UserPrincipal principal;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    principal = new CustomOAuth2UserPrincipal(userId, Map.of());
  }

  @Test
  @DisplayName("OAuth2 로그인 성공 시 JWT 발급 후 frontendUrl/callback?token=으로 리다이렉트")
  void onAuthenticationSuccess_redirectsWithToken() throws Exception {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    Authentication authentication = mock(Authentication.class);

    given(authentication.getPrincipal()).willReturn(principal);
    given(jwtProvider.createToken(userId)).willReturn("test-jwt-token");
    given(appProperties.frontendUrl()).willReturn("https://meet.chuseok22.com");

    // when
    successHandler.onAuthenticationSuccess(request, response, authentication);

    // then
    verify(jwtProvider).createToken(userId);
    verify(response).sendRedirect(contains("/callback?token=test-jwt-token"));
  }
}
