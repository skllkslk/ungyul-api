package com.ungyul.api.dailyreport;

import static org.assertj.core.api.Assertions.assertThat;

import com.ungyul.api.user.User;
import com.ungyul.api.user.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class DailyReportRepositoryTest {

  @Autowired
  private DailyReportRepository dailyReportRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  void findByUserId_해당_유저_데이터만_반환() {
    User user1 = userRepository.save(User.builder()
        .email("user1@test.com").nickname("유저1")
        .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());
    User user2 = userRepository.save(User.builder()
        .email("user2@test.com").nickname("유저2")
        .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());

    dailyReportRepository.save(DailyReport.builder()
        .user(user1).reportDate(LocalDate.of(2026, 4, 26))
        .mood("happy").content("유저1 내용").createdAt(LocalDateTime.now()).build());
    dailyReportRepository.save(DailyReport.builder()
        .user(user2).reportDate(LocalDate.of(2026, 4, 26))
        .mood("sad").content("유저2 내용").createdAt(LocalDateTime.now()).build());

    List<DailyReport> result = dailyReportRepository.findByUserId(user1.getId());

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getContent()).isEqualTo("유저1 내용");
  }

  @Test
  void findByUserId_데이터_없으면_빈_목록() {
    List<DailyReport> result = dailyReportRepository.findByUserId(999L);

    assertThat(result).isEmpty();
  }
}
