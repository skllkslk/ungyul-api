package com.ungyul.api.dailyreport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ungyul.api.user.User;
import com.ungyul.api.user.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DailyReportServiceTest {

  @InjectMocks
  private DailyReportService dailyReportService;

  @Mock
  private DailyReportRepository dailyReportRepository;

  @Mock
  private UserRepository userRepository;

  @Test
  void create_성공() {
    User user = User.builder()
        .id(1L)
        .email("test@test.com")
        .nickname("테스트")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    DailyReport saved = DailyReport.builder()
        .id(1L)
        .user(user)
        .reportDate(LocalDate.of(2026, 4, 26))
        .mood("happy")
        .content("오늘 좋은 날")
        .createdAt(LocalDateTime.now())
        .build();

    given(userRepository.findById(1L)).willReturn(Optional.of(user));
    given(dailyReportRepository.save(any())).willReturn(saved);

    CreateDailyReportRequest request = new CreateDailyReportRequest(
        LocalDate.of(2026, 4, 26), "happy", "오늘 좋은 날");

    DailyReportResponse response = dailyReportService.create(1L, request);

    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getReportDate()).isEqualTo(LocalDate.of(2026, 4, 26));
    assertThat(response.getMood()).isEqualTo("happy");
    assertThat(response.getContent()).isEqualTo("오늘 좋은 날");
  }

  @Test
  void create_존재하지_않는_유저() {
    given(userRepository.findById(999L)).willReturn(Optional.empty());

    CreateDailyReportRequest request = new CreateDailyReportRequest(
        LocalDate.of(2026, 4, 26), "happy", "내용");

    assertThatThrownBy(() -> dailyReportService.create(999L, request))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void getList_성공() {
    User user = User.builder().id(1L).build();
    List<DailyReport> reports = List.of(
        DailyReport.builder().id(1L).user(user)
            .reportDate(LocalDate.of(2026, 4, 26))
            .mood("happy").content("내용1").createdAt(LocalDateTime.now()).build(),
        DailyReport.builder().id(2L).user(user)
            .reportDate(LocalDate.of(2026, 4, 25))
            .mood("sad").content("내용2").createdAt(LocalDateTime.now()).build()
    );

    given(dailyReportRepository.findByUserId(1L)).willReturn(reports);

    List<DailyReportResponse> result = dailyReportService.getList(1L);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getMood()).isEqualTo("happy");
    assertThat(result.get(1).getMood()).isEqualTo("sad");
  }

  @Test
  void getList_빈_목록() {
    given(dailyReportRepository.findByUserId(1L)).willReturn(List.of());

    List<DailyReportResponse> result = dailyReportService.getList(1L);

    assertThat(result).isEmpty();
  }
}
