package com.chuseok22.timemateserver.admin.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification.telegram")
public record TelegramProperties(
    boolean enabled,
    String botToken,
    String chatId
) {

}
