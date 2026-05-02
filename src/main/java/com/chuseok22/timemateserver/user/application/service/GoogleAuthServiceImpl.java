package com.chuseok22.timemateserver.user.application.service;

import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.chuseok22.timemateserver.common.infrastructure.util.JwtProvider;
import com.chuseok22.timemateserver.user.application.dto.request.FirebaseGoogleLoginRequest;
import com.chuseok22.timemateserver.user.application.dto.response.GoogleLoginResponse;
import com.chuseok22.timemateserver.user.core.service.GoogleAuthService;
import com.chuseok22.timemateserver.user.core.service.UserService;
import com.chuseok22.timemateserver.user.infrastructure.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class GoogleAuthServiceImpl implements GoogleAuthService {

  private static final String GOOGLE_SIGN_IN_PROVIDER = "google.com";
  private static final String PROVIDER_GOOGLE = "google";

  private final UserService userService;
  private final JwtProvider jwtProvider;
  private final FirebaseAuth firebaseAuth;

  // GOOGLE_APPLICATION_CREDENTIALS 미설정 환경에서는 firebaseAuth가 null로 주입됨
  public GoogleAuthServiceImpl(
      UserService userService,
      JwtProvider jwtProvider,
      @Autowired(required = false) FirebaseAuth firebaseAuth) {
    this.userService = userService;
    this.jwtProvider = jwtProvider;
    this.firebaseAuth = firebaseAuth;
  }

  @Override
  @Transactional
  public GoogleLoginResponse loginWithFirebase(FirebaseGoogleLoginRequest request) {
    if (firebaseAuth == null) {
      log.error("Firebase Admin SDK 미설정 — GOOGLE_APPLICATION_CREDENTIALS 환경변수를 확인하세요");
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    FirebaseToken firebaseToken = verifyIdToken(request.firebaseIdToken());

    String signInProvider = extractSignInProvider(firebaseToken);
    if (!GOOGLE_SIGN_IN_PROVIDER.equals(signInProvider)) {
      log.warn("Google 로그인 아닌 Firebase 토큰 수신: provider={}", signInProvider);
      throw new CustomException(ErrorCode.FIREBASE_TOKEN_INVALID);
    }

    String firebaseUid = firebaseToken.getUid();
    String name = firebaseToken.getName();
    String email = firebaseToken.getEmail();

    User user = userService.findOrCreateUser(PROVIDER_GOOGLE, firebaseUid,
        name != null ? name : "", email);

    String token = jwtProvider.createToken(user.getId());
    log.info("Google Firebase 로그인 성공: userId={}", user.getId());
    return new GoogleLoginResponse(token);
  }

  private FirebaseToken verifyIdToken(String idToken) {
    try {
      return firebaseAuth.verifyIdToken(idToken);
    } catch (FirebaseAuthException e) {
      log.warn("Firebase ID Token 검증 실패: {}", e.getMessage());
      throw new CustomException(ErrorCode.FIREBASE_TOKEN_INVALID);
    }
  }

  @SuppressWarnings("unchecked")
  private String extractSignInProvider(FirebaseToken token) {
    Object firebaseClaim = token.getClaims().get("firebase");
    if (!(firebaseClaim instanceof Map<?, ?> claimMap)) {
      return "";
    }
    Object provider = claimMap.get("sign_in_provider");
    return provider instanceof String s ? s : "";
  }
}
