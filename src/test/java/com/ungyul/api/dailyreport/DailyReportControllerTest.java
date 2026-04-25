package com.ungyul.api.dailyreport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DailyReportController.class)
class DailyReportControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DailyReportService dailyReportService;

  @Test
  @WithMockUser
  void POST_daily_reports_성공() throws Exception {
    DailyReportResponse response = DailyReportResponse.builder()
        .id(1L)
        .reportDate(LocalDate.of(2026, 4, 26))
        .mood("happy")
        .content("오늘 좋은 날")
        .build();

    given(dailyReportService.create(eq(1L), any())).willReturn(response);

    mockMvc.perform(post("/api/daily-reports")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "reportDate": "2026-04-26",
                  "mood": "happy",
                  "content": "오늘 좋은 날"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.mood").value("happy"))
        .andExpect(jsonPath("$.content").value("오늘 좋은 날"));
  }

  @Test
  @WithMockUser
  void GET_daily_reports_성공() throws Exception {
    List<DailyReportResponse> responses = List.of(
        DailyReportResponse.builder()
            .id(1L).reportDate(LocalDate.of(2026, 4, 26)).mood("happy").content("내용1").build(),
        DailyReportResponse.builder()
            .id(2L).reportDate(LocalDate.of(2026, 4, 25)).mood("sad").content("내용2").build()
    );

    given(dailyReportService.getList(1L)).willReturn(responses);

    mockMvc.perform(get("/api/daily-reports").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].mood").value("happy"))
        .andExpect(jsonPath("$[1].mood").value("sad"));
  }

  @Test
  @WithMockUser
  void GET_daily_reports_빈_목록() throws Exception {
    given(dailyReportService.getList(1L)).willReturn(List.of());

    mockMvc.perform(get("/api/daily-reports").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }
}
