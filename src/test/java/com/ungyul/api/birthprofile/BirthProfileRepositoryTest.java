package com.ungyul.api.birthprofile;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BirthProfileRepositoryTest {

  @Autowired
  private BirthProfileRepository birthProfileRepository;

  @Test
  void findByUserId_저장된_프로필_반환() {
    birthProfileRepository.save(BirthProfile.builder()
        .userId(1L)
        .birthDate(LocalDate.of(1995, 3, 15))
        .birthTime(LocalTime.of(14, 30))
        .isLunar(false)
        .gender("MALE")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build());

    Optional<BirthProfile> result = birthProfileRepository.findByUserId(1L);

    assertThat(result).isPresent();
    assertThat(result.get().getBirthDate()).isEqualTo(LocalDate.of(1995, 3, 15));
    assertThat(result.get().getGender()).isEqualTo("MALE");
  }

  @Test
  void findByUserId_없으면_empty_반환() {
    Optional<BirthProfile> result = birthProfileRepository.findByUserId(999L);

    assertThat(result).isEmpty();
  }

  @Test
  void existsByUserId_존재하면_true() {
    birthProfileRepository.save(BirthProfile.builder()
        .userId(2L)
        .birthDate(LocalDate.of(1990, 6, 20))
        .isLunar(false)
        .gender("FEMALE")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build());

    assertThat(birthProfileRepository.existsByUserId(2L)).isTrue();
  }

  @Test
  void existsByUserId_없으면_false() {
    assertThat(birthProfileRepository.existsByUserId(999L)).isFalse();
  }

  @Test
  void findByUserId_다른_유저_데이터_반환_안됨() {
    birthProfileRepository.save(BirthProfile.builder()
        .userId(1L)
        .birthDate(LocalDate.of(1995, 3, 15))
        .isLunar(false)
        .gender("MALE")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build());

    Optional<BirthProfile> result = birthProfileRepository.findByUserId(2L);

    assertThat(result).isEmpty();
  }
}
