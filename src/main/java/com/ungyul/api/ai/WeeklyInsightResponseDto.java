package com.ungyul.api.ai;

import java.util.List;
import lombok.Getter;

@Getter
public class WeeklyInsightResponseDto {

  private String title;
  private String summary;
  private String interpretation;
  private List<String> actionSuggestions;
}
