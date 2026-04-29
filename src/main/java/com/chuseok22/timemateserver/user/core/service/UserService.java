package com.chuseok22.timemateserver.user.core.service;

import com.chuseok22.timemateserver.user.application.dto.response.UserInfoResponse;
import com.chuseok22.timemateserver.user.application.dto.response.UserRoomResponse;
import com.chuseok22.timemateserver.user.infrastructure.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {

  // OAuth2 로그인 시 사용자 조회 또는 신규 생성
  User findOrCreateUser(String provider, String providerId, String nickname, String email);

  UserInfoResponse getUserInfo(UUID userId);

  List<UserRoomResponse> getUserRooms(UUID userId);
}
