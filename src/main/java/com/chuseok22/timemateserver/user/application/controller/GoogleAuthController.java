package com.chuseok22.timemateserver.user.application.controller;

import com.chuseok22.timemateserver.common.application.aop.LogMonitoringInvocation;
import com.chuseok22.timemateserver.user.application.dto.request.FirebaseGoogleLoginRequest;
import com.chuseok22.timemateserver.user.application.dto.response.GoogleLoginResponse;
import com.chuseok22.timemateserver.user.core.service.GoogleAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/firebase")
@RequiredArgsConstructor
public class GoogleAuthController implements GoogleAuthControllerDocs {

  private final GoogleAuthService googleAuthService;

  @LogMonitoringInvocation
  @PostMapping("/google")
  public ResponseEntity<GoogleLoginResponse> loginWithGoogle(
      @Valid @RequestBody FirebaseGoogleLoginRequest request) {
    return ResponseEntity.ok(googleAuthService.loginWithFirebase(request));
  }
}
