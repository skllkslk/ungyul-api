package com.ungyul.api.sajuprofile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saju-profile")
@RequiredArgsConstructor
public class SajuProfileController {

  private static final Long TEMP_USER_ID = 1L;
  private final SajuProfileService sajuProfileService;

  @PostMapping("/generate")
  public ResponseEntity<SajuProfileResponse> generate() {
    return ResponseEntity.ok(sajuProfileService.generate(TEMP_USER_ID));
  }

  @GetMapping("/me")
  public ResponseEntity<SajuProfileResponse> getMyProfile() {
    return ResponseEntity.ok(sajuProfileService.getMyProfile(TEMP_USER_ID));
  }

}
