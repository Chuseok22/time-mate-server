package com.chuseok22.timemateserver.admin.infrastructure.config;

import com.chuseok22.timemateserver.admin.infrastructure.properties.TelegramProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramConfig {

}
