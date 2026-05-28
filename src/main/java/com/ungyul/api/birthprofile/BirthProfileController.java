package com.ungyul.api.birthprofile;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/birth-profile")
@RequiredArgsConstructor
public class BirthProfileController {

    private final BirthProfileService birthProfileService;

    @PostMapping
    public BirthProfileResponse saveOrUpdate(
            @AuthenticationPrincipal Long userId,
            @RequestBody BirthProfileRequest request) {
        return birthProfileService.saveOrUpdate(userId, request);
    }

    @GetMapping
    public BirthProfileResponse getMyProfile(@AuthenticationPrincipal Long userId) {
        return birthProfileService.getByUserId(userId);
    }
}
