package com.ungyul.api.insight;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightController {

    private final InsightService service;

    @PostMapping("/weekly/generate")
    public InsightReportResponse generate(@AuthenticationPrincipal Long userId) {
        return service.generateWeeklyInsight(userId);
    }

    @GetMapping("/weekly/latest")
    public InsightReportResponse getLatest(@AuthenticationPrincipal Long userId) {
        return service.getLatestWeeklyInsight(userId);
    }
}
