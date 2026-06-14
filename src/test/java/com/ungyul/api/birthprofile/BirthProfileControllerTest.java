package com.ungyul.api.birthprofile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
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
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(BirthProfileController.class)
class BirthProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BirthProfileService birthProfileService;

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

    private static final Long USER_ID = 1L;

    private static UsernamePasswordAuthenticationToken mockAuth() {
        return new UsernamePasswordAuthenticationToken(
                USER_ID, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void POST_birth_profile_저장_성공() throws Exception {
        BirthProfileResponse response = BirthProfileResponse.builder()
                .id(1L)
                .userId(USER_ID)
                .birthDate(LocalDate.of(1995, 3, 15))
                .birthTime(LocalTime.of(14, 30))
                .isLunar(false)
                .gender("MALE")
                .build();

        given(birthProfileService.saveOrUpdate(eq(USER_ID), any())).willReturn(response);

        mockMvc.perform(post("/api/birth-profile")
                        .with(csrf())
                        .with(authentication(mockAuth()))
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
    void GET_birth_profile_미등록_유저_404() throws Exception {
        willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "생년월일 프로필 없음"))
                .given(birthProfileService).getByUserId(USER_ID);

        mockMvc.perform(get("/api/birth-profile")
                        .with(csrf())
                        .with(authentication(mockAuth())))
                .andExpect(status().isNotFound());
    }

    @Test
    void GET_birth_profile_조회_성공() throws Exception {
        BirthProfileResponse response = BirthProfileResponse.builder()
                .id(1L)
                .userId(USER_ID)
                .birthDate(LocalDate.of(1995, 3, 15))
                .birthTime(LocalTime.of(14, 30))
                .isLunar(false)
                .gender("MALE")
                .build();

        given(birthProfileService.getByUserId(USER_ID)).willReturn(response);

        mockMvc.perform(get("/api/birth-profile")
                        .with(csrf())
                        .with(authentication(mockAuth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.birthDate").value("1995-03-15"))
                .andExpect(jsonPath("$.gender").value("MALE"));
    }
}
