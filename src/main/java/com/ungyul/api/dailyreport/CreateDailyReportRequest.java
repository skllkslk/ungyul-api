package com.ungyul.api.dailyreport;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDailyReportRequest {

  private LocalDate reportDate;
  private String mood;
  private String content;
}
