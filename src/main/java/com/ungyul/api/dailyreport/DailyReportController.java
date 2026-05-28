package com.ungyul.api.dailyreport;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping
    public DailyReportResponse create(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreateDailyReportRequest request) {
        return dailyReportService.create(userId, request);
    }

    @GetMapping
    public List<DailyReportResponse> getList(@AuthenticationPrincipal Long userId) {
        return dailyReportService.getList(userId);
    }
}
