package com.chuseok22.timemateserver.meeting.infrastructure.entity;

import com.chuseok22.timemateserver.common.infrastructure.persistence.BasePostgresEntity;
import com.chuseok22.timemateserver.meeting.core.constant.TimeSlot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
    name = "availability_time",
    uniqueConstraints = @UniqueConstraint(columnNames = {"participant_id", "meeting_date_id", "time_slot"})
)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailabilityTime extends BasePostgresEntity {

  @Id
  @Column(name = "availability_time_id")
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "participant_id", nullable = false)
  private Participant participant;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "meeting_date_id", nullable = false)
  private MeetingDate meetingDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TimeSlot timeSlot;

  public static AvailabilityTime create(Participant participant, MeetingDate meetingDate, TimeSlot timeSlot) {
    return AvailabilityTime.builder()
        .participant(participant)
        .meetingDate(meetingDate)
        .timeSlot(timeSlot)
        .build();
  }
}
