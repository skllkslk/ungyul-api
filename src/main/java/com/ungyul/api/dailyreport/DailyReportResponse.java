package com.ungyul.api.dailyreport;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyReportResponse {

  private Long id;
  private LocalDate reportDate;
  private String mood;
  private String content;
}
