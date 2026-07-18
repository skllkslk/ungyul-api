package com.ungyul.api.insight;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InsightReportResponse {

  private Long id;
  private String insightType;
  private LocalDate periodStartDate;
  private LocalDate periodEndDate;
  private String title;
  private String summary;
  private String interpretation;
  private List<String> actionSuggestions;
  private LocalDateTime createdAt;

  public static InsightReportResponse from(InsightReport report) {
    return InsightReportResponse.builder()
        .id(report.getId())
        .insightType(report.getInsightType())
        .periodStartDate(report.getPeriodStartDate())
        .periodEndDate(report.getPeriodEndDate())
        .title(report.getTitle())
        .summary(report.getSummary())
        .interpretation(report.getInterpretation())
        .actionSuggestions(splitSuggestions(report.getActionSuggestions()))
        .createdAt(report.getCreatedAt())
        .build();
  }

  private static List<String> splitSuggestions(String raw) {
    if (raw == null || raw.isBlank()) {
      return List.of();
    }
    return List.of(raw.split("\n"));
  }
}
