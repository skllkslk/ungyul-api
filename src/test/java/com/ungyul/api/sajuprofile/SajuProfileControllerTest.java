package com.ungyul.api.sajuprofile;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SajuProfileController.class)
class SajuProfileControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SajuProfileService sajuProfileService;

  private SajuProfileResponse response() {
    return SajuProfileResponse.builder()
        .profileId(1L)
        .userId(1L)
        .birthInfoId(10L)
        .dayMaster("갑")
        .profileText("갑목 일간 프로필 텍스트")
        .sajuRawJson("{\"dayMaster\":\"갑\"}")
        .createdAt(LocalDateTime.of(2026, 5, 1, 0, 0))
        .updatedAt(LocalDateTime.of(2026, 5, 1, 0, 0))
        .build();
  }

  @Test
  @WithMockUser
  void POST_saju_profile_generate_성공() throws Exception {
    given(sajuProfileService.generate(1L)).willReturn(response());

    mockMvc.perform(post("/api/saju-profile/generate").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.profileId").value(1L))
        .andExpect(jsonPath("$.userId").value(1L))
        .andExpect(jsonPath("$.birthInfoId").value(10L))
        .andExpect(jsonPath("$.dayMaster").value("갑"))
        .andExpect(jsonPath("$.profileText").value("갑목 일간 프로필 텍스트"));
  }

  @Test
  @WithMockUser
  void GET_saju_profile_me_성공() throws Exception {
    given(sajuProfileService.getMyProfile(1L)).willReturn(response());

    mockMvc.perform(get("/api/saju-profile/me").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.profileId").value(1L))
        .andExpect(jsonPath("$.dayMaster").value("갑"))
        .andExpect(jsonPath("$.profileText").value("갑목 일간 프로필 텍스트"));
  }
}
