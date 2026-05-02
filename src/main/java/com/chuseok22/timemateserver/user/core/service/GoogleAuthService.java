package com.chuseok22.timemateserver.user.core.service;

import com.chuseok22.timemateserver.user.application.dto.request.FirebaseGoogleLoginRequest;
import com.chuseok22.timemateserver.user.application.dto.response.GoogleLoginResponse;

public interface GoogleAuthService {

  // Firebase ID Token 검증 후 서비스 JWT 발급
  GoogleLoginResponse loginWithFirebase(FirebaseGoogleLoginRequest request);
}
