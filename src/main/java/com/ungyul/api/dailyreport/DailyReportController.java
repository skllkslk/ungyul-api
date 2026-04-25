package com.ungyul.api.dailyreport;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/daily-reports")
@RequiredArgsConstructor
public class DailyReportController {

  private final DailyReportService dailyReportService;
  private static final Long TEMP_USER_ID = 1L;

  @PostMapping
  public DailyReportResponse create(@RequestBody CreateDailyReportRequest request) {
    return dailyReportService.create(TEMP_USER_ID, request);
  }

  @GetMapping
  public List<DailyReportResponse> getList() {
    return dailyReportService.getList(TEMP_USER_ID);
  }
}
