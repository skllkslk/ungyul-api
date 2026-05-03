package com.ungyul.api.birthprofile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BirthProfileServiceTest {

  @InjectMocks
  private BirthProfileService birthProfileService;

  @Mock
  private BirthProfileRepository birthProfileRepository;

  @Test
  void saveOrUpdate_신규_저장_성공() {
    BirthProfileRequest request = new BirthProfileRequest();

    BirthProfile saved = BirthProfile.builder()
        .id(1L)
        .userId(1L)
        .birthDate(LocalDate.of(1995, 3, 15))
        .birthTime(LocalTime.of(14, 30))
        .isLunar(false)
        .gender("MALE")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(birthProfileRepository.findByUserId(1L)).willReturn(Optional.empty());
    given(birthProfileRepository.save(any())).willReturn(saved);

    BirthProfileResponse response = birthProfileService.saveOrUpdate(1L, request);

    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getUserId()).isEqualTo(1L);
    assertThat(response.getBirthDate()).isEqualTo(LocalDate.of(1995, 3, 15));
    assertThat(response.getIsLunar()).isFalse();
    assertThat(response.getGender()).isEqualTo("MALE");
    verify(birthProfileRepository).save(any());
  }

  @Test
  void saveOrUpdate_기존_프로필_업데이트_성공() {
    BirthProfileRequest request = new BirthProfileRequest();

    BirthProfile existing = BirthProfile.builder()
        .id(1L)
        .userId(1L)
        .birthDate(LocalDate.of(1990, 1, 1))
        .birthTime(LocalTime.of(8, 0))
        .isLunar(true)
        .gender("FEMALE")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(birthProfileRepository.findByUserId(1L)).willReturn(Optional.of(existing));

    BirthProfileResponse response = birthProfileService.saveOrUpdate(1L, request);

    assertThat(response.getId()).isEqualTo(1L);
    verify(birthProfileRepository, never()).save(any());
  }

  @Test
  void getByUserId_조회_성공() {
    BirthProfile profile = BirthProfile.builder()
        .id(1L)
        .userId(1L)
        .birthDate(LocalDate.of(1995, 3, 15))
        .birthTime(LocalTime.of(14, 30))
        .isLunar(false)
        .gender("MALE")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    given(birthProfileRepository.findByUserId(1L)).willReturn(Optional.of(profile));

    BirthProfileResponse response = birthProfileService.getByUserId(1L);

    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getBirthDate()).isEqualTo(LocalDate.of(1995, 3, 15));
    assertThat(response.getGender()).isEqualTo("MALE");
  }

  @Test
  void getByUserId_프로필_없으면_예외() {
    given(birthProfileRepository.findByUserId(999L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> birthProfileService.getByUserId(999L))
        .isInstanceOf(NoSuchElementException.class);
  }
}
