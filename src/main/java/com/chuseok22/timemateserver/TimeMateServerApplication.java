package com.chuseok22.timemateserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TimeMateServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(TimeMateServerApplication.class, args);
  }

}
