package com.chuseok22.timemateserver.common.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FirebaseAdminConfig {

  // GOOGLE_APPLICATION_CREDENTIALS 환경변수가 설정된 경우에만 Bean 등록
  @Bean
  @ConditionalOnExpression("T(java.lang.System).getenv('GOOGLE_APPLICATION_CREDENTIALS') != null")
  public FirebaseAuth firebaseAuth() throws IOException {
    List<FirebaseApp> apps = FirebaseApp.getApps();
    if (apps.isEmpty()) {
      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.getApplicationDefault())
          .build();
      FirebaseApp.initializeApp(options);
    }
    log.info("Firebase Admin SDK 초기화 완료");
    return FirebaseAuth.getInstance();
  }
}
