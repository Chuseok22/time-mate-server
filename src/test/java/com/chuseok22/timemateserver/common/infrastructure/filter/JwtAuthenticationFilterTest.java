package com.chuseok22.timemateserver.common.infrastructure.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.common.infrastructure.util.JwtProvider;
import jakarta.servlet.FilterChain;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock
  private JwtProvider jwtProvider;

  private JwtAuthenticationFilter filter;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private FilterChain chain;

  @BeforeEach
  void setUp() {
    filter = new JwtAuthenticationFilter(jwtProvider);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    chain = mock(FilterChain.class);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("유효한 JWT: SecurityContext에 인증 정보 설정 후 체인 계속")
  void validJwt_setsAuthentication_continuesChain() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
    given(jwtProvider.getUserId("valid-token")).willReturn(userId);

    // when
    filter.doFilterInternal(request, response, chain);

    // then
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(userId);
    verify(chain).doFilter(request, response);
  }

  @Test
  @DisplayName("만료된 JWT: 401 TOKEN_EXPIRED 반환, 체인 중단")
  void expiredJwt_returns401TokenExpired_stopsChain() throws Exception {
    // given
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer expired-token");
    given(jwtProvider.getUserId("expired-token"))
        .willThrow(new CustomException(ErrorCode.TOKEN_EXPIRED));

    // when
    filter.doFilterInternal(request, response, chain);

    // then
    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getContentAsString()).contains("TOKEN_EXPIRED");
    verify(chain, never()).doFilter(any(), any());
  }

  @Test
  @DisplayName("유효하지 않은 JWT: 401 INVALID_TOKEN 반환, 체인 중단")
  void invalidJwt_returns401InvalidToken_stopsChain() throws Exception {
    // given
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid-token");
    given(jwtProvider.getUserId("invalid-token"))
        .willThrow(new CustomException(ErrorCode.INVALID_TOKEN));

    // when
    filter.doFilterInternal(request, response, chain);

    // then
    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getContentAsString()).contains("INVALID_TOKEN");
    verify(chain, never()).doFilter(any(), any());
  }

  @Test
  @DisplayName("Authorization 헤더 없음: 인증 없이 체인 계속")
  void noAuthHeader_noAuthentication_continuesChain() throws Exception {
    // when
    filter.doFilterInternal(request, response, chain);

    // then
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(chain).doFilter(request, response);
  }

  @Test
  @DisplayName("Bearer 아닌 헤더: 인증 없이 체인 계속")
  void nonBearerHeader_noAuthentication_continuesChain() throws Exception {
    // given
    request.addHeader(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNz");

    // when
    filter.doFilterInternal(request, response, chain);

    // then
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(chain).doFilter(request, response);
  }
}
