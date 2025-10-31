package com.chuseok22.timemateserver.admin.application.service;

import com.chuseok22.timemateserver.admin.core.service.AdminNotifier;
import com.chuseok22.timemateserver.admin.infrastructure.properties.TelegramProperties;
import com.chuseok22.timemateserver.common.core.exception.CustomException;
import com.chuseok22.timemateserver.common.core.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramNotifier implements AdminNotifier {

  private final OkHttpClient okHttpClient;
  private final ObjectMapper objectMapper;
  private final TelegramProperties properties;

  private static final MediaType APPLICATION_JSON = MediaType.get("application/json; charset=utf-8");

  @Async
  @Override
  public void notifyRoomCreated(String roomTitle) {
    if (!properties.enabled()) {
      log.info("텔레그램 알림이 비활성화 되어있습니다.");
      return;
    }

    String message = "새로운 방이 생성되었습니다. 방 제목: " + roomTitle;
    try {
      sendMessage(message);
      log.info("텔레그램 알림 발송 완료");
    } catch (Exception e) {
      log.warn("텔레그램 알림 발송 실패: {}", e.getMessage());
    }
  }

  private void sendMessage(String message) throws IOException {
    String url = "https://api.telegram.org/bot" + properties.botToken() + "/sendMessage";

    byte[] json = objectMapper.writeValueAsBytes(Map.of("chat_id", properties.chatId(), "text", message));

    RequestBody body = RequestBody.create(json, APPLICATION_JSON);

    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new CustomException(ErrorCode.TELEGRAM_MESSAGE_SEND_FAILED);
      }
    }
  }
}
