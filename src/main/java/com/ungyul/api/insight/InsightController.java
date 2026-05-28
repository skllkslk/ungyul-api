package com.ungyul.api.insight;

import com.ungyul.api.ai.WeeklyInsightResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightController {

    private final InsightService service;

    @PostMapping("/weekly/generate")
    public WeeklyInsightResponseDto generate(@AuthenticationPrincipal Long userId) {
        return service.generateWeeklyInsight(userId);
    }
}
