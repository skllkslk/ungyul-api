package com.ungyul.api.sajuprofile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saju-profile")
@RequiredArgsConstructor
public class SajuProfileController {

    private final SajuProfileService sajuProfileService;

    @PostMapping("/generate")
    public ResponseEntity<SajuProfileResponse> generate(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(sajuProfileService.generate(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<SajuProfileResponse> getMyProfile(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(sajuProfileService.getMyProfile(userId));
    }
}
