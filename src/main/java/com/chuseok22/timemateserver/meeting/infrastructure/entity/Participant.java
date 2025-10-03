package com.chuseok22.timemateserver.meeting.infrastructure.entity;

import com.chuseok22.timemateserver.common.core.util.CommonUtil;
import com.chuseok22.timemateserver.common.infrastructure.persistence.BasePostgresEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant extends BasePostgresEntity {

  @Id
  @Column(name = "participant_id")
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "meeting_room_id", nullable = false)
  private MeetingRoom meetingRoom;

  @Column(nullable = false)
  private String username;

  private String password;

  public static Participant create(MeetingRoom room, String username, String password) {
    return Participant.builder()
        .meetingRoom(room)
        .username(username.trim())
        .password(CommonUtil.nvl(password, "").isEmpty() ? null : password)
        .build();
  }
}
