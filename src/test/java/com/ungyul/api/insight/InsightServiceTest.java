package com.ungyul.api.insight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ungyul.api.ai.AiClient;
import com.ungyul.api.ai.WeeklyInsightResponseDto;
import com.ungyul.api.dailyreport.DailyReport;
import com.ungyul.api.dailyreport.DailyReportRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InsightServiceTest {

  @Mock
  private DailyReportRepository dailyReportRepository;

  @Mock
  private AiClient aiClient;

  @Mock
  private InsightReportRepository insightReportRepository;

  @InjectMocks
  private InsightService insightService;

  @Test
  @DisplayName("주간 인사이트를 생성하고 결과를 저장한다")
  void generateWeeklyInsight() {
    // given
    Long userId = 1L;

    DailyReport report = DailyReport.builder()
        .reportDate(LocalDate.now())
        .mood("피곤함")
        .content("오늘은 집중이 잘 안 됐다.")
        .build();

    WeeklyInsightResponseDto aiResponse = new WeeklyInsightResponseDto(
        "이번 주 운결 흐름",
        "회복과 정리가 중요합니다.",
        "피로와 집중 저하가 반복됩니다.",
        List.of("목표 줄이기", "수면 점검하기")
    );

    given(dailyReportRepository.findByUserIdAndReportDateBetween(
        anyLong(),
        any(LocalDate.class),
        any(LocalDate.class)
    )).willReturn(List.of(report));

    given(aiClient.generateWeeklyInsight(any()))
        .willReturn(aiResponse);

    given(insightReportRepository.save(any(InsightReport.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when
    WeeklyInsightResponseDto result = insightService.generateWeeklyInsight(userId);

    // then
    assertThat(result.getTitle()).isEqualTo("이번 주 운결 흐름");

    verify(aiClient).generateWeeklyInsight(any());
    verify(insightReportRepository).save(argThat(saved ->
        saved.getUserId().equals(userId)
            && saved.getInsightType().equals("WEEKLY")
            && saved.getTitle().equals("이번 주 운결 흐름")
            && saved.getActionSuggestions().contains("목표 줄이기")
    ));
  }
}
