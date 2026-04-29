package com.chuseok22.timemateserver.common.infrastructure.config;

import com.chuseok22.timemateserver.common.infrastructure.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

}
