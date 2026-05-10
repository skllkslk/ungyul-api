package com.ungyul.api.sajuprofile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saju_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SajuProfile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private Long birthInfoId;

  @Column(length = 50)
  private String dayMaster;

  @Column(columnDefinition = "TEXT")
  private String profileText;

  @Column(columnDefinition = "TEXT")
  private String sajuRawJson;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public SajuProfile(Long userId, Long birthInfoId, String dayMaster, String profileText, String sajuRawJson) {
    this.userId = userId;
    this.birthInfoId = birthInfoId;
    this.dayMaster = dayMaster;
    this.profileText = profileText;
    this.sajuRawJson = sajuRawJson;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public void updateProfile(String dayMaster, String profileText, String sajuRawJson) {
    this.dayMaster = dayMaster;
    this.profileText = profileText;
    this.sajuRawJson = sajuRawJson;
    this.updatedAt = LocalDateTime.now();
  }
}
