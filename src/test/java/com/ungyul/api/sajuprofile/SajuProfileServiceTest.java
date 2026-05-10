package com.ungyul.api.sajuprofile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.ungyul.api.ai.AiClient;
import com.ungyul.api.ai.SajuCalculateResponseDto;
import com.ungyul.api.birthprofile.BirthProfile;
import com.ungyul.api.birthprofile.BirthProfileRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SajuProfileServiceTest {

  @InjectMocks
  private SajuProfileService sajuProfileService;

  @Mock
  private SajuProfileRepository sajuProfileRepository;

  @Mock
  private BirthProfileRepository birthProfileRepository;

  @Mock
  private AiClient aiClient;

  private BirthProfile birthProfile() {
    return BirthProfile.builder()
        .id(10L)
        .userId(1L)
        .birthDate(LocalDate.of(1995, 3, 15))
        .birthTime(LocalTime.of(14, 30))
        .isLunar(false)
        .gender("MALE")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private SajuCalculateResponseDto calculatedResponse() {
    SajuCalculateResponseDto dto = mock(SajuCalculateResponseDto.class);
    given(dto.getDayMaster()).willReturn("갑");
    given(dto.getProfileText()).willReturn("갑목 일간 프로필 텍스트");
    given(dto.getSajuRawJson()).willReturn("{\"dayMaster\":\"갑\"}");
    return dto;
  }

  @Test
  void generate_신규_생성_성공() {
    BirthProfile birth = birthProfile();
    SajuCalculateResponseDto calculated = calculatedResponse();
    SajuProfile saved = new SajuProfile(1L, 10L, "갑", "갑목 일간 프로필 텍스트", "{\"dayMaster\":\"갑\"}");

    given(birthProfileRepository.findByUserId(1L)).willReturn(Optional.of(birth));
    given(aiClient.calculateSaju(any())).willReturn(calculated);
    given(sajuProfileRepository.findByUserId(1L)).willReturn(Optional.empty());
    given(sajuProfileRepository.save(any())).willReturn(saved);

    SajuProfileResponse response = sajuProfileService.generate(1L);

    assertThat(response.getDayMaster()).isEqualTo("갑");
    assertThat(response.getProfileText()).isEqualTo("갑목 일간 프로필 텍스트");
    assertThat(response.getBirthInfoId()).isEqualTo(10L);
    verify(sajuProfileRepository).save(any(SajuProfile.class));
  }

  @Test
  void generate_기존_프로필_업데이트_성공() {
    BirthProfile birth = birthProfile();
    SajuCalculateResponseDto calculated = calculatedResponse();
    SajuProfile existing = new SajuProfile(1L, 10L, "을", "구 프로필 텍스트", "{}");

    given(birthProfileRepository.findByUserId(1L)).willReturn(Optional.of(birth));
    given(aiClient.calculateSaju(any())).willReturn(calculated);
    given(sajuProfileRepository.findByUserId(1L)).willReturn(Optional.of(existing));
    given(sajuProfileRepository.save(existing)).willReturn(existing);

    SajuProfileResponse response = sajuProfileService.generate(1L);

    assertThat(response.getDayMaster()).isEqualTo("갑");
    verify(sajuProfileRepository).save(existing);
  }

  @Test
  void generate_출생정보_없으면_예외() {
    given(birthProfileRepository.findByUserId(999L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> sajuProfileService.generate(999L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("출생정보가 존재하지 않습니다.");
  }

  @Test
  void getMyProfile_조회_성공() {
    SajuProfile profile = new SajuProfile(1L, 10L, "갑", "갑목 프로필", "{\"dayMaster\":\"갑\"}");

    given(sajuProfileRepository.findByUserId(1L)).willReturn(Optional.of(profile));

    SajuProfileResponse response = sajuProfileService.getMyProfile(1L);

    assertThat(response.getDayMaster()).isEqualTo("갑");
    assertThat(response.getProfileText()).isEqualTo("갑목 프로필");
    assertThat(response.getUserId()).isEqualTo(1L);
  }

  @Test
  void getMyProfile_프로필_없으면_예외() {
    given(sajuProfileRepository.findByUserId(999L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> sajuProfileService.getMyProfile(999L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("사주 프로필이 존재하지 않습니다.");
  }
}
