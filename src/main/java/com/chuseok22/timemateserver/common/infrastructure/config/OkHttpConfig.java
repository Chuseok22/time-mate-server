package com.chuseok22.timemateserver.common.infrastructure.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OkHttpConfig {

  @Bean
  public OkHttpClient okHttpClient() {
    return new OkHttpClient.Builder()
        .connectTimeout(Duration.ofSeconds(2))
        .readTimeout(Duration.ofSeconds(3))
        .writeTimeout(Duration.ofSeconds(3))
        .callTimeout(Duration.ofSeconds(5))
        .retryOnConnectionFailure(false)
        .connectionPool(new ConnectionPool(5, 60, TimeUnit.SECONDS))
        .build();
  }
}
