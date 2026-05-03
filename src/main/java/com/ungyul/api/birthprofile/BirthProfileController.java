package com.ungyul.api.birthprofile;

import lombok.RequiredArgsConstructor;
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
  private static final Long TEMP_USER_ID = 1L;

  @PostMapping
  public BirthProfileResponse saveOuUpdate(@RequestBody BirthProfileRequest request) {
    return birthProfileService.saveOrUpdate(TEMP_USER_ID, request);
  }

  @GetMapping
  public BirthProfileResponse getMyProfile() {
    return birthProfileService.getByUserId(TEMP_USER_ID);
  }

}
