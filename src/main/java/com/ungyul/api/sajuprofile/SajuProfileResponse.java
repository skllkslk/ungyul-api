package com.ungyul.api.sajuprofile;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SajuProfileResponse {

  private Long profileId;
  private Long userId;
  private Long birthInfoId;
  private String dayMaster;
  private String profileText;
  private String sajuRawJson;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static SajuProfileResponse from(SajuProfile profile) {
    return SajuProfileResponse.builder()
        .profileId(profile.getId())
        .userId(profile.getUserId())
        .birthInfoId(profile.getBirthInfoId())
        .dayMaster(profile.getDayMaster())
        .profileText(profile.getProfileText())
        .sajuRawJson(profile.getSajuRawJson())
        .createdAt(profile.getCreatedAt())
        .updatedAt(profile.getUpdatedAt())
        .build();
  }
}
