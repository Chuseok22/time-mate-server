package com.chuseok22.timemateserver.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.infrastructure.util.JwtProvider;
import com.chuseok22.timemateserver.user.application.dto.request.FirebaseGoogleLoginRequest;
import com.chuseok22.timemateserver.user.application.dto.response.GoogleLoginResponse;
import com.chuseok22.timemateserver.user.core.service.UserService;
import com.chuseok22.timemateserver.user.infrastructure.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceImplTest {

  @Mock
  private UserService userService;

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private FirebaseAuth firebaseAuth;

  private GoogleAuthServiceImpl googleAuthService;
  private GoogleAuthServiceImpl googleAuthServiceWithoutFirebase;

  @BeforeEach
  void setUp() {
    googleAuthService = new GoogleAuthServiceImpl(userService, jwtProvider, firebaseAuth);
    googleAuthServiceWithoutFirebase = new GoogleAuthServiceImpl(userService, jwtProvider, null);
  }

  @Test
  @DisplayName("유효한 Firebase Google ID Token으로 로그인 성공")
  void loginWithFirebase_success() throws FirebaseAuthException {
    // given
    String idToken = "valid-firebase-id-token";
    String firebaseUid = "firebase-uid-123";
    UUID userId = UUID.randomUUID();

    FirebaseToken firebaseToken = mock(FirebaseToken.class);
    given(firebaseToken.getUid()).willReturn(firebaseUid);
    given(firebaseToken.getName()).willReturn("홍길동");
    given(firebaseToken.getEmail()).willReturn("hong@gmail.com");
    given(firebaseToken.getClaims()).willReturn(
        Map.of("firebase", Map.of("sign_in_provider", "google.com"))
    );

    User user = mock(User.class);
    given(user.getId()).willReturn(userId);

    given(firebaseAuth.verifyIdToken(idToken)).willReturn(firebaseToken);
    given(userService.findOrCreateUser(eq("google"), eq(firebaseUid), any(), any()))
        .willReturn(user);
    given(jwtProvider.createToken(userId)).willReturn("service-jwt-token");

    // when
    GoogleLoginResponse response = googleAuthService.loginWithFirebase(
        new FirebaseGoogleLoginRequest(idToken));

    // then
    assertThat(response.accessToken()).isEqualTo("service-jwt-token");
  }

  @Test
  @DisplayName("Firebase Admin SDK 미설정 시 INTERNAL_SERVER_ERROR 발생")
  void loginWithFirebase_firebaseNotConfigured_throwsException() {
    // given
    FirebaseGoogleLoginRequest request = new FirebaseGoogleLoginRequest("some-token");

    // when & then
    assertThatThrownBy(() -> googleAuthServiceWithoutFirebase.loginWithFirebase(request))
        .isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("잘못된 Firebase ID Token 수신 시 FIREBASE_TOKEN_INVALID 발생")
  void loginWithFirebase_invalidToken_throwsException() throws FirebaseAuthException {
    // given
    String invalidToken = "invalid-token";
    given(firebaseAuth.verifyIdToken(invalidToken)).willThrow(mock(FirebaseAuthException.class));

    // when & then
    assertThatThrownBy(() -> googleAuthService.loginWithFirebase(
        new FirebaseGoogleLoginRequest(invalidToken)))
        .isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("Google 이외의 provider 토큰 수신 시 FIREBASE_TOKEN_INVALID 발생")
  void loginWithFirebase_nonGoogleProvider_throwsException() throws FirebaseAuthException {
    // given
    String idToken = "kakao-firebase-token";
    FirebaseToken firebaseToken = mock(FirebaseToken.class);
    given(firebaseToken.getClaims()).willReturn(
        Map.of("firebase", Map.of("sign_in_provider", "oidc.kakao"))
    );
    given(firebaseAuth.verifyIdToken(idToken)).willReturn(firebaseToken);

    // when & then
    assertThatThrownBy(() -> googleAuthService.loginWithFirebase(
        new FirebaseGoogleLoginRequest(idToken)))
        .isInstanceOf(CustomException.class);
  }
}
