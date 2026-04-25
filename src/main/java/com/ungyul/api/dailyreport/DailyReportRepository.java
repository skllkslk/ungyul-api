package com.ungyul.api.dailyreport;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

  List<DailyReport> findByUserId(Long userId);
}
