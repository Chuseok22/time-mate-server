package com.chuseok22.timemateserver.common.infrastructure.util;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.common.infrastructure.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtProvider {

  private final JwtProperties jwtProperties;
  // 생성자에서 한 번 생성 후 재사용 (매 호출마다 재생성하면 비효율)
  private final SecretKey signingKey;

  public JwtProvider(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret()));
  }

  public String createToken(UUID userId) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + jwtProperties.expirationMs());

    return Jwts.builder()
        .subject(userId.toString())
        .issuedAt(now)
        .expiration(expiration)
        .signWith(signingKey)
        .compact();
  }

  public UUID getUserId(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(signingKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();
      return UUID.fromString(claims.getSubject());
    } catch (ExpiredJwtException e) {
      log.warn("JWT 만료: {}", e.getMessage());
      throw new CustomException(ErrorCode.TOKEN_EXPIRED);
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("JWT 검증 실패: {}", e.getMessage());
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }
}
