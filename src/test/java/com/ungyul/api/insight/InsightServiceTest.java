package com.ungyul.api.insight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ungyul.api.ai.AiClient;
import com.ungyul.api.ai.WeeklyInsightResponseDto;
import com.ungyul.api.dailyreport.DailyReport;
import com.ungyul.api.dailyreport.DailyReportRepository;
import com.ungyul.api.sajuprofile.SajuProfileRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class InsightServiceTest {

  @Mock
  private DailyReportRepository dailyReportRepository;

  @Mock
  private AiClient aiClient;

  @Mock
  private InsightReportRepository insightReportRepository;

  @Mock
  private SajuProfileRepository sajuProfileRepository;

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
    InsightReportResponse result = insightService.generateWeeklyInsight(userId);

    // then
    assertThat(result.getTitle()).isEqualTo("이번 주 운결 흐름");
    assertThat(result.getActionSuggestions())
        .containsExactly("목표 줄이기", "수면 점검하기");

    verify(aiClient).generateWeeklyInsight(any());
    verify(insightReportRepository).save(argThat(saved ->
        saved.getUserId().equals(userId)
            && saved.getInsightType().equals("WEEKLY")
            && saved.getTitle().equals("이번 주 운결 흐름")
            && saved.getActionSuggestions().contains("목표 줄이기")
    ));
  }

  @Test
  @DisplayName("가장 최근 주간 인사이트를 조회한다")
  void getLatestWeeklyInsight() {
    // given
    Long userId = 1L;
    InsightReport saved = InsightReport.builder()
        .id(10L)
        .userId(userId)
        .insightType("WEEKLY")
        .periodStartDate(LocalDate.of(2026, 7, 13))
        .periodEndDate(LocalDate.of(2026, 7, 19))
        .title("이번 주 운결 흐름")
        .summary("회복과 정리가 중요합니다.")
        .interpretation("피로와 집중 저하가 반복됩니다.")
        .actionSuggestions("목표 줄이기\n수면 점검하기")
        .createdAt(LocalDateTime.now())
        .build();

    given(insightReportRepository
        .findTopByUserIdAndInsightTypeOrderByCreatedAtDesc(userId, "WEEKLY"))
        .willReturn(Optional.of(saved));

    // when
    InsightReportResponse result = insightService.getLatestWeeklyInsight(userId);

    // then
    assertThat(result.getId()).isEqualTo(10L);
    assertThat(result.getTitle()).isEqualTo("이번 주 운결 흐름");
    assertThat(result.getActionSuggestions())
        .containsExactly("목표 줄이기", "수면 점검하기");
  }

  @Test
  @DisplayName("주간 인사이트가 없으면 404 예외가 발생한다")
  void getLatestWeeklyInsight_notFound() {
    // given
    Long userId = 1L;
    given(insightReportRepository
        .findTopByUserIdAndInsightTypeOrderByCreatedAtDesc(userId, "WEEKLY"))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> insightService.getLatestWeeklyInsight(userId))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND));
  }
}
