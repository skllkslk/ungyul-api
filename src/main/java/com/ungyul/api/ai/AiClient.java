package com.ungyul.api.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AiClient {

  private final WebClient fastApiWebClient;

  @Value("${ai.fastapi.internal-api-key}")
  private String internalApiKey;

  public WeeklyInsightResponseDto generateWeeklyInsight(WeeklyInsightRequestDto request) {
    return fastApiWebClient.post()
        .uri("/ai/insights/weekly/generate")
        .header("X-Internal-Api-Key", internalApiKey)
        .bodyValue(request)
        .retrieve()
        .bodyToMono(WeeklyInsightResponseDto.class)
        .block();
  }
}
