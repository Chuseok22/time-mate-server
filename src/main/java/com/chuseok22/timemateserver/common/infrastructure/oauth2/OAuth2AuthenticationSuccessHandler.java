package com.chuseok22.timemateserver.common.infrastructure.oauth2;

import com.chuseok22.timemateserver.common.infrastructure.properties.AppProperties;
import com.chuseok22.timemateserver.common.infrastructure.util.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtProvider jwtProvider;
  private final AppProperties appProperties;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {
    CustomOAuth2UserPrincipal principal = (CustomOAuth2UserPrincipal) authentication.getPrincipal();
    String token = jwtProvider.createToken(principal.getUserId());
    String redirectUrl = appProperties.frontendUrl() + "/callback?token=" + token;
    log.info("OAuth2 로그인 성공, 리다이렉트: userId={}", principal.getUserId());
    response.sendRedirect(redirectUrl);
  }
}
