package com.ungyul.api.insight;

import com.ungyul.api.ai.AiClient;
import com.ungyul.api.ai.WeeklyInsightRequestDto;
import com.ungyul.api.ai.WeeklyInsightResponseDto;
import com.ungyul.api.dailyreport.DailyReport;
import com.ungyul.api.dailyreport.DailyReportRepository;
import com.ungyul.api.sajuprofile.SajuProfileRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class InsightService {

  private final DailyReportRepository dailyReportRepository;
  private final AiClient aiClient;
  private final InsightReportRepository insightReportRepository;
  private final SajuProfileRepository sajuProfileRepository;

  public InsightReportResponse generateWeeklyInsight(Long userId) {
    LocalDate start = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    LocalDate end = start.plusDays(6);

    Optional<InsightReport> existing = insightReportRepository
        .findByUserIdAndInsightTypeAndPeriodStartDate(userId, "WEEKLY", start);
    if (existing.isPresent()) {
      return InsightReportResponse.from(existing.get());
    }

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
        .actionSuggestions(String.join("\n", response.getActionSuggestions()))
        .createdAt(LocalDateTime.now())
        .build();
    return InsightReportResponse.from(insightReportRepository.save(insightReport));
  }

  public InsightReportResponse getLatestWeeklyInsight(Long userId) {
    InsightReport report = insightReportRepository
        .findTopByUserIdAndInsightTypeOrderByCreatedAtDesc(userId, "WEEKLY")
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "주간 리포트가 아직 없습니다."));
    return InsightReportResponse.from(report);
  }

}
