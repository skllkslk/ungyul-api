package com.ungyul.api.birthprofile;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public class BirthProfileRequest {

  private LocalDate birthDate;
  private LocalTime birthTime;
  private Boolean isLunar;
  private String gender;
}
