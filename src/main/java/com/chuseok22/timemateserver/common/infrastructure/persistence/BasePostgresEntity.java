package com.chuseok22.timemateserver.common.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@ToString
@SuperBuilder
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BasePostgresEntity {

  // 생성일
  @CreatedDate
  @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
  private LocalDateTime createdAt;

  // 수정일
  @LastModifiedDate
  @Column(nullable = false, columnDefinition = "TIMESTAMP(0)")
  private LocalDateTime updatedAt;

  // 삭제여부
  @Builder.Default
  @Column(nullable = false)
  private boolean isDeleted = false;
}
