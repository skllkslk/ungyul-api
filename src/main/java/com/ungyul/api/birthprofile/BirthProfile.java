package com.ungyul.api.birthprofile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="birth_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BirthProfile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private LocalDate birthDate;

  private LocalTime birthTime;

  private Boolean isLunar;

  private String gender;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public void update(
      LocalDate birthDate,
      LocalTime birthTime,
      Boolean isLunar,
      String gender
  ) {
    this.birthDate = birthDate;
    this.birthTime = birthTime;
    this.isLunar = isLunar;
    this.gender = gender;
    this.updatedAt = LocalDateTime.now();
  }
}
