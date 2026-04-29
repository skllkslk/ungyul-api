package com.ungyul.api.dailyreport;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

  List<DailyReport> findByUserIdAndReportDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

  List<DailyReport> findByUserId(Long userId);
}
