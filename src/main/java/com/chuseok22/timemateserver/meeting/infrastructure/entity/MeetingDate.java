package com.chuseok22.timemateserver.meeting.infrastructure.entity;

import com.chuseok22.timemateserver.common.infrastructure.persistence.BasePostgresEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
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
public class MeetingDate extends BasePostgresEntity {

  @Id
  @Column(name = "meeting_date_id")
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(nullable = false)
  private MeetingRoom meetingRoom;

  @Column(nullable = false)
  private LocalDate date;

  public static MeetingDate create(MeetingRoom meetingRoom, LocalDate date) {
    return MeetingDate.builder()
        .meetingRoom(meetingRoom)
        .date(date)
        .build();
  }
}
