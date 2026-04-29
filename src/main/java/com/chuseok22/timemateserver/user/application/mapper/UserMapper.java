package com.chuseok22.timemateserver.user.application.mapper;

import com.chuseok22.timemateserver.meeting.infrastructure.entity.MeetingRoom;
import com.chuseok22.timemateserver.user.application.dto.response.UserInfoResponse;
import com.chuseok22.timemateserver.user.application.dto.response.UserRoomResponse;
import com.chuseok22.timemateserver.user.infrastructure.entity.User;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  // User 엔티티를 내 정보 응답 DTO로 변환
  public UserInfoResponse toUserInfoResponse(User user) {
    return new UserInfoResponse(user.getId(), user.getNickname(), user.getEmail());
  }

  // MeetingRoom을 사용자 방 목록 응답 DTO로 변환 (방장 여부 포함)
  public UserRoomResponse toUserRoomResponse(MeetingRoom room, UUID userId) {
    boolean isOwner = userId.equals(room.getCreatorUserId());
    return new UserRoomResponse(room.getId(), room.getTitle(), room.getJoinCode(), isOwner);
  }
}
