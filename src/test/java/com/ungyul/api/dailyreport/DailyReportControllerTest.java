package com.ungyul.api.dailyreport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ungyul.api.common.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DailyReportController.class)
class DailyReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DailyReportService dailyReportService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final Long USER_ID = 1L;

    // mock 필터는 chain.doFilter를 호출하지 않아 요청이 컨트롤러에 도달하지 못하므로 통과시키도록 stub
    @BeforeEach
    void passThroughJwtFilter() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    private static UsernamePasswordAuthenticationToken mockAuth() {
        return new UsernamePasswordAuthenticationToken(
                USER_ID, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void POST_daily_reports_성공() throws Exception {
        DailyReportResponse response = DailyReportResponse.builder()
                .id(1L)
                .reportDate(LocalDate.of(2026, 4, 26))
                .mood("happy")
                .content("오늘 좋은 날")
                .energy(4)
                .tags(List.of("행복", "여행"))
                .build();

        given(dailyReportService.create(eq(USER_ID), any())).willReturn(response);

        mockMvc.perform(post("/api/daily-reports")
                        .with(csrf())
                        .with(authentication(mockAuth()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reportDate": "2026-04-26",
                                  "mood": "happy",
                                  "content": "오늘 좋은 날",
                                  "energy": 4,
                                  "tags": ["행복", "여행"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.mood").value("happy"))
                .andExpect(jsonPath("$.content").value("오늘 좋은 날"))
                .andExpect(jsonPath("$.energy").value(4))
                .andExpect(jsonPath("$.tags[0]").value("행복"))
                .andExpect(jsonPath("$.tags[1]").value("여행"));
    }

    @Test
    void GET_daily_reports_성공() throws Exception {
        List<DailyReportResponse> responses = List.of(
                DailyReportResponse.builder()
                        .id(1L).reportDate(LocalDate.of(2026, 4, 26)).mood("happy").content("내용1").build(),
                DailyReportResponse.builder()
                        .id(2L).reportDate(LocalDate.of(2026, 4, 25)).mood("sad").content("내용2").build()
        );

        given(dailyReportService.getList(USER_ID)).willReturn(responses);

        mockMvc.perform(get("/api/daily-reports")
                        .with(csrf())
                        .with(authentication(mockAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].mood").value("happy"))
                .andExpect(jsonPath("$[1].mood").value("sad"));
    }

    @Test
    void GET_daily_reports_빈_목록() throws Exception {
        given(dailyReportService.getList(USER_ID)).willReturn(List.of());

        mockMvc.perform(get("/api/daily-reports")
                        .with(csrf())
                        .with(authentication(mockAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
