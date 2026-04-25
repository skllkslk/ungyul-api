package com.ungyul.api.dailyreport;

import com.ungyul.api.user.User;
import com.ungyul.api.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyReportService {

  private final DailyReportRepository dailyReportRepository;
  private final UserRepository userRepository;

  public DailyReportResponse create(Long userId, CreateDailyReportRequest request) {

    User user = userRepository.findById(userId)
        .orElseThrow();

    DailyReport report = DailyReport.builder()
        .user(user)
        .reportDate(request.getReportDate())
        .mood(request.getMood())
        .content(request.getContent())
        .createdAt(LocalDateTime.now())
        .build();

    DailyReport saved = dailyReportRepository.save(report);

    return DailyReportResponse.builder()
        .id(saved.getId())
        .reportDate(saved.getReportDate())
        .mood(saved.getMood())
        .content(saved.getContent())
        .build();
  }

  public List<DailyReportResponse> getList(Long userId) {
    return dailyReportRepository.findByUserId(userId)
        .stream()
        .map(r -> DailyReportResponse.builder()
            .id(r.getId())
            .reportDate(r.getReportDate())
            .mood(r.getMood())
            .content(r.getContent())
            .build())
        .toList();
  }

}
