package com.ungyul.api.sajuprofile;

import com.ungyul.api.ai.AiClient;
import com.ungyul.api.ai.SajuCalculateRequestDto;
import com.ungyul.api.ai.SajuCalculateResponseDto;
import com.ungyul.api.birthprofile.BirthProfile;
import com.ungyul.api.birthprofile.BirthProfileRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SajuProfileService {

  private final SajuProfileRepository sajuProfileRepository;
  private final BirthProfileRepository birthProfileRepository;
  private final AiClient aiClient;

  @Transactional
  public SajuProfileResponse generate(Long userId) {
    BirthProfile birthProfile = birthProfileRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("출생정보가 존재하지 않습니다."));

    SajuCalculateResponseDto calculated = aiClient.calculateSaju(
        SajuCalculateRequestDto.builder()
            .userId(userId)
            .birthDate(birthProfile.getBirthDate())
            .birthTime(birthProfile.getBirthTime())
            .isLunar(birthProfile.getIsLunar())
            .gender(birthProfile.getGender())
            .build()
    );

    Optional<SajuProfile> existingProfile = sajuProfileRepository.findByUserId(userId);

    SajuProfile profile = existingProfile
        .map(existing -> {
          existing.updateProfile(
              calculated.getDayMaster(),
              calculated.getProfileText(),
              calculated.getSajuRawJson()
          );
          return existing;
        })
        .orElseGet(() -> new SajuProfile(
            userId,
            birthProfile.getId(),
            calculated.getDayMaster(),
            calculated.getProfileText(),
            calculated.getSajuRawJson()
        ));

    SajuProfile saved = sajuProfileRepository.save(profile);
    return SajuProfileResponse.from(saved);
  }

  public SajuProfileResponse getMyProfile(Long userId) {
    SajuProfile profile = sajuProfileRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("사주 프로필이 존재하지 않습니다."));

    return SajuProfileResponse.from(profile);
  }
}
