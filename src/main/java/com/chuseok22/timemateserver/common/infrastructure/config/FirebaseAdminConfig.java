package com.chuseok22.timemateserver.common.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
public class FirebaseAdminConfig {

  private static final String CLASSPATH_CREDENTIAL = "firebase-adminsdk.json";

  /**
   * Firebase Admin SDK 초기화.
   *
   * 세 가지 자격증명 소스 중 하나가 존재할 때만 Bean 등록:
   * - FIREBASE_SERVICE_ACCOUNT_JSON : 서비스 계정 JSON 내용 (env var)
   * - GOOGLE_APPLICATION_CREDENTIALS : 서비스 계정 JSON 파일 경로 (env var)
   * - classpath:firebase-adminsdk.json : CI/CD 빌드 시 GitHub Secret에서 주입되는 파일
   */
  @Bean
  @ConditionalOnExpression(
      "T(java.lang.System).getenv('FIREBASE_SERVICE_ACCOUNT_JSON') != null"
          + " || T(java.lang.System).getenv('GOOGLE_APPLICATION_CREDENTIALS') != null"
          + " || new org.springframework.core.io.ClassPathResource('firebase-adminsdk.json').exists()"
  )
  public FirebaseAuth firebaseAuth() throws IOException {
    List<FirebaseApp> apps = FirebaseApp.getApps();
    if (apps.isEmpty()) {
      GoogleCredentials credentials = buildCredentials();
      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(credentials)
          .build();
      FirebaseApp.initializeApp(options);
    }
    log.info("Firebase Admin SDK 초기화 완료");
    return FirebaseAuth.getInstance();
  }

  private GoogleCredentials buildCredentials() throws IOException {
    // 1순위: JSON 내용 직접 주입 (env var)
    String serviceAccountJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
    if (serviceAccountJson != null) {
      return GoogleCredentials.fromStream(
          new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8))
      );
    }
    // 2순위: CI/CD 빌드 시 classpath에 주입된 파일 (classpath:firebase-adminsdk.json)
    ClassPathResource classPathResource = new ClassPathResource(CLASSPATH_CREDENTIAL);
    if (classPathResource.exists()) {
      log.info("classpath:{} 로 Firebase 자격증명 로드", CLASSPATH_CREDENTIAL);
      return GoogleCredentials.fromStream(classPathResource.getInputStream());
    }
    // 3순위: 파일 경로 방식 (GOOGLE_APPLICATION_CREDENTIALS env var)
    return GoogleCredentials.getApplicationDefault();
  }
}
