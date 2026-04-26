package com.ungyul.api.insight;

import com.ungyul.api.ai.AiClient;
import com.ungyul.api.ai.WeeklyInsightRequestDto;
import com.ungyul.api.ai.WeeklyInsightResponseDto;
import com.ungyul.api.dailyreport.DailyReport;
import com.ungyul.api.dailyreport.DailyReportRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsightService {

  private final DailyReportRepository dailyReportRepository;
  private final AiClient aiClient;

  public WeeklyInsightResponseDto generateWeeklyInsight(Long userId) {

    LocalDate end = LocalDate.now();
    LocalDate start = end.minusDays(7);

    List<DailyReport> reports = dailyReportRepository.findByUserId(userId);

    var request = WeeklyInsightRequestDto.builder()
        .userId(userId)
        .periodStartDate(start)
        .periodEndDate(end)
        .profileSummary("기본 프로필 요약 (추후 연결)")
        .dailyReports(
            reports.stream()
                .map(r -> WeeklyInsightRequestDto.DailyReportItem.builder()
                    .date(r.getReportDate())
                    .mood(r.getMood())
                    .content(r.getContent())
                    .build())
                .toList()
        )
        .build();

    return aiClient.generateWeeklyInsight(request);
  }
}
