package com.ungyul.api.ai;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyInsightResponseDto {

  private String title;
  private String summary;
  private String interpretation;
  private List<String> actionSuggestions;
}
