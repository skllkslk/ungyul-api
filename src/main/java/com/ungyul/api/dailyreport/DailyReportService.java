package com.ungyul.api.dailyreport;

import com.ungyul.api.user.User;
import com.ungyul.api.user.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
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
        .energy(request.getEnergy())
        .tags(joinTags(request.getTags()))
        .createdAt(LocalDateTime.now())
        .build();

    DailyReport saved = dailyReportRepository.save(report);

    return toResponse(saved);
  }

  public List<DailyReportResponse> getList(Long userId) {
    return dailyReportRepository.findByUserId(userId)
        .stream()
        .map(this::toResponse)
        .toList();
  }

  private DailyReportResponse toResponse(DailyReport report) {
    return DailyReportResponse.builder()
        .id(report.getId())
        .reportDate(report.getReportDate())
        .mood(report.getMood())
        .content(report.getContent())
        .energy(report.getEnergy())
        .tags(splitTags(report.getTags()))
        .build();
  }

  private String joinTags(List<String> tags) {
    return (tags == null || tags.isEmpty()) ? null : String.join(",", tags);
  }

  private List<String> splitTags(String tags) {
    return (tags == null || tags.isBlank()) ? List.of() : Arrays.asList(tags.split(","));
  }

}
