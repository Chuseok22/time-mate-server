package com.chuseok22.timemateserver.common.infrastructure.config;

import com.chuseok22.timemateserver.common.infrastructure.filter.JwtAuthenticationFilter;
import com.chuseok22.timemateserver.common.infrastructure.oauth2.CustomOAuth2UserService;
import com.chuseok22.timemateserver.common.infrastructure.oauth2.OAuth2AuthenticationSuccessHandler;
import com.chuseok22.timemateserver.common.infrastructure.properties.AppProperties;
import com.chuseok22.timemateserver.common.infrastructure.util.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CorsConfigurationSource corsConfigurationSource;
  private final JwtProvider jwtProvider;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
  private final AppProperties appProperties;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // Stateless JWT 인증 방식이므로 CSRF 토큰 불필요
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            // 사용자 정보 API: 인증 필수
            .requestMatchers("/api/users/**").authenticated()
            // 방/참가자 삭제 API: 인증 필수
            .requestMatchers(HttpMethod.DELETE, "/api/rooms/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/participant/**").authenticated()
            // 나머지 API: 공개 (Guest 포함 허용)
            .anyRequest().permitAll()
        )
        // oauth2Login()의 기본 EntryPoint가 OAuth2 URL로 리다이렉트하므로
        // REST API 인증 실패 시 401 JSON을 반환하도록 명시적으로 재정의
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) -> {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
              response.getWriter().write(
                  "{\"errorCode\":\"UNAUTHENTICATED\",\"errorMessage\":\"인증이 필요합니다.\"}"
              );
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
              response.getWriter().write(
                  "{\"errorCode\":\"ACCESS_DENIED\",\"errorMessage\":\"접근이 거부되었습니다.\"}"
              );
            })
        )
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(ui -> ui.userService(customOAuth2UserService))
            .successHandler(oAuth2AuthenticationSuccessHandler)
            // OAuth2 인증 실패 시 프론트엔드 에러 페이지로 리다이렉트
            .failureHandler((request, response, exception) -> {
              log.warn("OAuth2 인증 실패: {}", exception.getMessage());
              response.sendRedirect(appProperties.frontendUrl() + "/login?error=oauth2_failed");
            })
        )
        .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
