package com.chuseok22.timemateserver.meeting.infrastructure.properties;

import com.chuseok22.timemateserver.meeting.core.constant.JoinCodeAlphabet;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "join-code")
public record JoinCodeProperties(
    JoinCodeAlphabet alphabet,
    int length,
    int maxRetries
) {

}
