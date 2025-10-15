package com.chuseok22.timemateserver.meeting.infrastructure.config;

import com.chuseok22.timemateserver.meeting.infrastructure.properties.JoinCodeProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JoinCodeProperties.class)
public class JoinCodeConfig {

}
