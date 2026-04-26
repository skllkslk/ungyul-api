package com.ungyul.api.ai;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyInsightRequestDto {

  private Long userId;
  private LocalDate periodStartDate;
  private LocalDate periodEndDate;
  private String profileSummary;
  private List<DailyReportItem> dailyReports;

  @Getter
  @Builder
  public static class DailyReportItem {
    private LocalDate date;
    private String mood;
    private String content;
  }
}
