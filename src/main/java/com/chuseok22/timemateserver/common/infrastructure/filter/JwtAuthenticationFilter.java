package com.chuseok22.timemateserver.common.infrastructure.filter;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.infrastructure.util.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        UUID userId = jwtProvider.getUserId(token);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userId,
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            );
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (CustomException e) {
        // 토큰이 존재하지만 유효하지 않은 경우: TOKEN_EXPIRED / INVALID_TOKEN 을 직접 반환
        log.warn("JWT 인증 실패: errorCode={}, uri={}", e.getErrorCode(), request.getRequestURI());
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.getWriter().write(
            "{\"errorCode\":\"" + e.getErrorCode().name()
                + "\",\"errorMessage\":\"" + e.getErrorCode().getMessage() + "\"}"
        );
        return;
      }
    }

    chain.doFilter(request, response);
  }
}
