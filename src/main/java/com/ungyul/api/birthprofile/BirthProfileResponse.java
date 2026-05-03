package com.ungyul.api.birthprofile;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BirthProfileResponse {

  private Long id;
  private Long userId;
  private LocalDate birthDate;
  private LocalTime birthTime;
  private Boolean isLunar;
  private String gender;

}
