package com.chuseok22.timemateserver.user.infrastructure.entity;

import com.chuseok22.timemateserver.common.infrastructure.persistence.BasePostgresEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BasePostgresEntity {

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  // 소셜 제공자 구분 (google, kakao)
  @Column(nullable = false, length = 20)
  private String provider;

  // 소셜 제공자가 발급한 고유 식별자
  @Column(name = "provider_id", nullable = false)
  private String providerId;

  @Column(nullable = false)
  private String nickname;

  private String email;

  public static User create(String provider, String providerId, String nickname, String email) {
    return User.builder()
        .provider(provider)
        .providerId(providerId)
        .nickname(nickname)
        .email(email)
        .build();
  }

  // 닉네임 변경 (소셜 계정 정보 갱신용)
  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }
}
