package com.ungyul.api.auth;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ungyul.api.auth.dto.TokenResponse;
import com.ungyul.api.common.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(AuthController.class)
@WithMockUser
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        Mockito.doAnswer(inv -> {
            inv.<FilterChain>getArgument(2).doFilter(
                    inv.getArgument(0, ServletRequest.class),
                    inv.getArgument(1, ServletResponse.class));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(
                Mockito.any(ServletRequest.class),
                Mockito.any(ServletResponse.class),
                Mockito.any(FilterChain.class));
    }

    @Test
    void POST_google_로그인_성공() throws Exception {
        given(authService.login("valid-google-id-token"))
                .willReturn(new TokenResponse("access-token", "refresh-token", 1L));

        mockMvc.perform(post("/api/auth/google")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idToken": "valid-google-id-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void POST_google_로그인_빈_토큰_400() throws Exception {
        mockMvc.perform(post("/api/auth/google")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idToken": ""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void POST_google_로그인_유효하지_않은_토큰_401() throws Exception {
        given(authService.login("invalid-token"))
                .willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Google 토큰"));

        mockMvc.perform(post("/api/auth/google")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idToken": "invalid-token"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void POST_refresh_성공() throws Exception {
        given(authService.refresh("valid-refresh-token"))
                .willReturn(new TokenResponse("new-access-token", "valid-refresh-token", 1L));

        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken": "valid-refresh-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }

    @Test
    void POST_refresh_만료된_토큰_401() throws Exception {
        given(authService.refresh("expired-token"))
                .willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 Refresh Token"));

        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken": "expired-token"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void POST_logout_성공() throws Exception {
        willDoNothing().given(authService).logout("valid-refresh-token");

        mockMvc.perform(post("/api/auth/logout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken": "valid-refresh-token"}
                                """))
                .andExpect(status().isNoContent());
    }
}
