package com.chuseok22.timemateserver.common.infrastructure.config;

import com.chuseok22.timemateserver.common.infrastructure.properties.AppProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class AppConfig {}
