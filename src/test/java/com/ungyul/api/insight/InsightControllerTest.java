package com.ungyul.api.insight;

import static org.mockito.ArgumentMatchers.any;
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
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(InsightController.class)
class InsightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InsightService insightService;

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

    private static InsightReportResponse sampleResponse() {
        return InsightReportResponse.builder()
                .id(10L)
                .insightType("WEEKLY")
                .periodStartDate(LocalDate.of(2026, 7, 13))
                .periodEndDate(LocalDate.of(2026, 7, 19))
                .title("이번 주 운결 흐름")
                .summary("회복과 정리가 중요합니다.")
                .interpretation("피로와 집중 저하가 반복됩니다.")
                .actionSuggestions(List.of("목표 줄이기", "수면 점검하기"))
                .createdAt(LocalDateTime.of(2026, 7, 19, 10, 0))
                .build();
    }

    @Test
    void GET_weekly_latest_성공() throws Exception {
        given(insightService.getLatestWeeklyInsight(USER_ID)).willReturn(sampleResponse());

        mockMvc.perform(get("/api/insights/weekly/latest")
                        .with(csrf())
                        .with(authentication(mockAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.title").value("이번 주 운결 흐름"))
                .andExpect(jsonPath("$.actionSuggestions.length()").value(2))
                .andExpect(jsonPath("$.actionSuggestions[0]").value("목표 줄이기"));
    }

    @Test
    void GET_weekly_latest_리포트_없으면_404() throws Exception {
        given(insightService.getLatestWeeklyInsight(USER_ID))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "주간 리포트가 아직 없습니다."));

        mockMvc.perform(get("/api/insights/weekly/latest")
                        .with(csrf())
                        .with(authentication(mockAuth())))
                .andExpect(status().isNotFound());
    }

    @Test
    void POST_weekly_generate_성공() throws Exception {
        given(insightService.generateWeeklyInsight(USER_ID)).willReturn(sampleResponse());

        mockMvc.perform(post("/api/insights/weekly/generate")
                        .with(csrf())
                        .with(authentication(mockAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.summary").value("회복과 정리가 중요합니다."));
    }
}
