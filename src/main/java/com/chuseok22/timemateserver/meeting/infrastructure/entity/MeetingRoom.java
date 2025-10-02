package com.chuseok22.timemateserver.meeting.infrastructure.entity;

import com.chuseok22.timemateserver.common.infrastructure.persistence.BasePostgresEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "meeting_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingRoom extends BasePostgresEntity {

  @Id
  @Column(name = "meeting_room_id")
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String title;

  public static MeetingRoom create(String title) {
    return MeetingRoom.builder()
        .title(title)
        .build();
  }
}
