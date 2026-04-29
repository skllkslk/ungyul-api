package com.ungyul.api.insight;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsightReportRepository extends JpaRepository<InsightReport, Long> {

  Optional<InsightReport> findTopByUserIdAndInsightTypeOrderByCreatedAtDesc(
      Long userId,
      String insightType
  );
}
