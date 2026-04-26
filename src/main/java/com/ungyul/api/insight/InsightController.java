package com.ungyul.api.insight;

import com.ungyul.api.ai.WeeklyInsightResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightController {

  private final InsightService service;

  private static final Long TEMP_USER_ID = 1L;

  @PostMapping("/weekly/generate")
  public WeeklyInsightResponseDto generate() {
    return service.generateWeeklyInsight(TEMP_USER_ID);
  }
}
