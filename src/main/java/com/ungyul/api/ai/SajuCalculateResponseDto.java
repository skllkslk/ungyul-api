package com.ungyul.api.ai;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SajuCalculateResponseDto {

  private Long userId;
  private String solarDate;
  private String dayMaster;
  private String dayMasterDescription;
  private String profileText;
  private String sajuRawJson;
}
