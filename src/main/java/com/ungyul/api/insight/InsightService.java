package com.ungyul.api.insight;

import com.ungyul.api.ai.AiClient;
import com.ungyul.api.ai.WeeklyInsightRequestDto;
import com.ungyul.api.ai.WeeklyInsightResponseDto;
import com.ungyul.api.dailyreport.DailyReport;
import com.ungyul.api.dailyreport.DailyReportRepository;
import com.ungyul.api.sajuprofile.SajuProfileRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsightService {

  private final DailyReportRepository dailyReportRepository;
  private final AiClient aiClient;
  private final InsightReportRepository insightReportRepository;
  private final SajuProfileRepository sajuProfileRepository;

  public WeeklyInsightResponseDto generateWeeklyInsight(Long userId) {
    LocalDate end = LocalDate.now();
    LocalDate start = end.minusDays(6);

    String profileSummary = sajuProfileRepository.findByUserId(userId)
        .map(p -> p.getProfileText())
        .orElse(null);

    List<DailyReport> reports = dailyReportRepository.findByUserIdAndReportDateBetween(userId,
        start, end);
    WeeklyInsightRequestDto request = WeeklyInsightRequestDto.builder()
        .userId(userId)
        .periodStartDate(start)
        .periodEndDate(end)
        .profileSummary(profileSummary)
        .dailyReports(
            reports.stream()
                .map(report -> WeeklyInsightRequestDto.DailyReportItem.builder()
                    .date(report.getReportDate())
                    .mood(report.getMood())
                    .content(report.getContent())
                    .build())
                .toList()
        ).build();

    WeeklyInsightResponseDto response = aiClient.generateWeeklyInsight(request);
    InsightReport insightReport = InsightReport.builder()
        .userId(userId)
        .insightType("WEEKLY")
        .periodStartDate(start)
        .periodEndDate(end)
        .title(response.getTitle())
        .summary(response.getSummary())
        .interpretation(response.getInterpretation())
        .actionSuggestions(String.join(",", response.getActionSuggestions()))
        .createdAt(LocalDateTime.now())
        .build();
    insightReportRepository.save(insightReport);
    return response;
  }

}
