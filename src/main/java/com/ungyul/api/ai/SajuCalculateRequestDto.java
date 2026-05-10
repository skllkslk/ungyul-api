package com.ungyul.api.ai;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SajuCalculateRequestDto {

  private Long userId;
  private LocalDate birthDate;
  private LocalTime birthTime;
  private Boolean isLunar;
  private String gender;
}
