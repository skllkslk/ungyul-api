package com.ungyul.api.sajuprofile;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SajuProfileRepositoryTest {

  @Autowired
  private SajuProfileRepository sajuProfileRepository;

  @Test
  void findByUserId_조회_성공() {
    sajuProfileRepository.save(new SajuProfile(1L, 10L, "갑", "갑목 프로필", "{\"dayMaster\":\"갑\"}"));

    Optional<SajuProfile> result = sajuProfileRepository.findByUserId(1L);

    assertThat(result).isPresent();
    assertThat(result.get().getDayMaster()).isEqualTo("갑");
    assertThat(result.get().getProfileText()).isEqualTo("갑목 프로필");
  }

  @Test
  void findByUserId_없으면_empty() {
    Optional<SajuProfile> result = sajuProfileRepository.findByUserId(999L);

    assertThat(result).isEmpty();
  }

  @Test
  void existsByUserId_존재하면_true() {
    sajuProfileRepository.save(new SajuProfile(1L, 10L, "갑", "갑목 프로필", "{}"));

    assertThat(sajuProfileRepository.existsByUserId(1L)).isTrue();
  }

  @Test
  void existsByUserId_없으면_false() {
    assertThat(sajuProfileRepository.existsByUserId(999L)).isFalse();
  }

  @Test
  void findByUserId_다른_유저_프로필은_반환하지_않음() {
    sajuProfileRepository.save(new SajuProfile(1L, 10L, "갑", "유저1 프로필", "{}"));
    sajuProfileRepository.save(new SajuProfile(2L, 20L, "을", "유저2 프로필", "{}"));

    Optional<SajuProfile> result = sajuProfileRepository.findByUserId(1L);

    assertThat(result).isPresent();
    assertThat(result.get().getUserId()).isEqualTo(1L);
    assertThat(result.get().getDayMaster()).isEqualTo("갑");
  }
}
