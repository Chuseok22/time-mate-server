package com.chuseok22.timemateserver.user.application.controller;

import com.chuseok22.apichangelog.annotation.ApiChangeLog;
import com.chuseok22.apichangelog.annotation.ApiChangeLogs;
import com.chuseok22.timemateserver.user.application.dto.request.FirebaseGoogleLoginRequest;
import com.chuseok22.timemateserver.user.application.dto.response.GoogleLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "소셜 로그인 인증 API")
public interface GoogleAuthControllerDocs {

  @ApiChangeLogs({
      @ApiChangeLog(
          date = "2026-05-02",
          author = "Chuseok22",
          description = "Firebase 기반 Google 소셜 로그인 API 추가",
          issueUrl = "https://github.com/Chuseok22/meet-time-server/issues/33"
      )
  })
  @Operation(
      summary = "Google 소셜 로그인 (Firebase)",
      description = """
          ### 요청 파라미터
          - `firebaseIdToken` (String, 필수): Firebase Authentication SDK에서 발급받은 ID Token
            - 프론트엔드에서 `user.getIdToken(true)` 로 취득

          ### 응답 데이터
          - `accessToken` (String): 서비스 JWT. 이후 API 요청 시 `Authorization: Bearer <token>` 헤더에 포함

          ### 유의 사항
          - Firebase Google 로그인(`signInWithPopup` 또는 `signInWithRedirect`) 완료 후 취득한 Firebase ID Token을 전달해야 합니다.
          - Google 이외의 Firebase 로그인 토큰(카카오 등) 은 401 반환
          - 토큰 만료 또는 위조 시 401 반환
          - 서버에 `GOOGLE_APPLICATION_CREDENTIALS` 환경변수 미설정 시 500 반환
          """
  )
  ResponseEntity<GoogleLoginResponse> loginWithGoogle(
      @Valid @RequestBody FirebaseGoogleLoginRequest request
  );
}
