package com.chuseok22.timemateserver.common.infrastructure.config;

import com.chuseok22.timemateserver.common.infrastructure.filter.JwtAuthenticationFilter;
import com.chuseok22.timemateserver.common.infrastructure.oauth2.CustomOAuth2UserService;
import com.chuseok22.timemateserver.common.infrastructure.oauth2.OAuth2AuthenticationSuccessHandler;
import com.chuseok22.timemateserver.common.infrastructure.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CorsConfigurationSource corsConfigurationSource;
  private final JwtProvider jwtProvider;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

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
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(ui -> ui.userService(customOAuth2UserService))
            .successHandler(oAuth2AuthenticationSuccessHandler)
        )
        .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
