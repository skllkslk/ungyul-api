package com.ungyul.api.birthprofile;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BirthProfileService {

  private final BirthProfileRepository birthProfileRepository;

  @Transactional
  public BirthProfileResponse saveOrUpdate(Long userId, BirthProfileRequest request) {
    BirthProfile profile = birthProfileRepository.findByUserId(userId)
        .map(existing -> {
          existing.update(
              request.getBirthDate(),
              request.getBirthTime(),
              request.getIsLunar(),
              request.getGender()
          );
          return existing;
        })
        .orElseGet(() -> birthProfileRepository.save(
            BirthProfile.builder()
                .userId(userId)
                .birthDate(request.getBirthDate())
                .birthTime(request.getBirthTime())
                .isLunar(request.getIsLunar())
                .gender(request.getGender())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        ));

    return toResponse(profile);
  }

  @Transactional(readOnly = true)
  public BirthProfileResponse getByUserId(Long userId) {
    BirthProfile profile = birthProfileRepository.findByUserId(userId)
        .orElseThrow();
    return toResponse(profile);
  }

  private BirthProfileResponse toResponse(BirthProfile profile) {
    return BirthProfileResponse.builder()
        .id(profile.getId())
        .userId(profile.getUserId())
        .birthDate(profile.getBirthDate())
        .birthTime(profile.getBirthTime())
        .isLunar(profile.getIsLunar())
        .gender(profile.getGender())
        .build();
  }

}
