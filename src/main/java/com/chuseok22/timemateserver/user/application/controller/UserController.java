package com.chuseok22.timemateserver.user.application.controller;

import com.chuseok22.timemateserver.common.application.aop.LogMonitoringInvocation;
import com.chuseok22.timemateserver.user.application.controller.docs.UserControllerDocs;
import com.chuseok22.timemateserver.user.application.dto.response.UserInfoResponse;
import com.chuseok22.timemateserver.user.application.dto.response.UserRoomResponse;
import com.chuseok22.timemateserver.user.core.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

  private final UserService userService;

  // JWT 인증 후 SecurityContext의 principal(userId)을 기반으로 내 정보 조회
  @LogMonitoringInvocation
  @GetMapping("/me")
  public ResponseEntity<UserInfoResponse> getMyInfo(Authentication authentication) {
    UUID userId = (UUID) authentication.getPrincipal();
    return ResponseEntity.ok(userService.getUserInfo(userId));
  }

  // JWT 인증 후 SecurityContext의 principal(userId)을 기반으로 참여 방 목록 조회
  @LogMonitoringInvocation
  @GetMapping("/me/rooms")
  public ResponseEntity<List<UserRoomResponse>> getMyRooms(Authentication authentication) {
    UUID userId = (UUID) authentication.getPrincipal();
    return ResponseEntity.ok(userService.getUserRooms(userId));
  }

  @LogMonitoringInvocation
  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteUser(Authentication authentication) {
    UUID userId = (UUID) authentication.getPrincipal();
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }
}
