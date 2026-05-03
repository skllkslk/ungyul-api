package com.ungyul.api.birthprofile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BirthProfileController.class)
class BirthProfileControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BirthProfileService birthProfileService;

  @Test
  @WithMockUser
  void POST_birth_profile_저장_성공() throws Exception {
    BirthProfileResponse response = BirthProfileResponse.builder()
        .id(1L)
        .userId(1L)
        .birthDate(LocalDate.of(1995, 3, 15))
        .birthTime(LocalTime.of(14, 30))
        .isLunar(false)
        .gender("MALE")
        .build();

    given(birthProfileService.saveOrUpdate(eq(1L), any())).willReturn(response);

    mockMvc.perform(post("/api/birth-profile")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "birthDate": "1995-03-15",
                  "birthTime": "14:30:00",
                  "isLunar": false,
                  "gender": "MALE"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.userId").value(1L))
        .andExpect(jsonPath("$.birthDate").value("1995-03-15"))
        .andExpect(jsonPath("$.isLunar").value(false))
        .andExpect(jsonPath("$.gender").value("MALE"));
  }

  @Test
  @WithMockUser
  void GET_birth_profile_조회_성공() throws Exception {
    BirthProfileResponse response = BirthProfileResponse.builder()
        .id(1L)
        .userId(1L)
        .birthDate(LocalDate.of(1995, 3, 15))
        .birthTime(LocalTime.of(14, 30))
        .isLunar(false)
        .gender("MALE")
        .build();

    given(birthProfileService.getByUserId(1L)).willReturn(response);

    mockMvc.perform(get("/api/birth-profile").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.birthDate").value("1995-03-15"))
        .andExpect(jsonPath("$.gender").value("MALE"));
  }
}
