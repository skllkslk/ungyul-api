package com.ungyul.api.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ungyul.api.auth.GoogleTokenVerifier.GoogleUserInfo;
import com.ungyul.api.auth.dto.TokenResponse;
import com.ungyul.api.common.security.JwtProvider;
import com.ungyul.api.user.User;
import com.ungyul.api.user.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private GoogleTokenVerifier googleTokenVerifier;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshTokenExpirationDays", 30);
    }

    @Test
    void login_신규_유저_가입_후_토큰_반환() {
        given(googleTokenVerifier.verify("valid-id-token"))
                .willReturn(new GoogleUserInfo("google-sub-123", "test@gmail.com"));
        given(userRepository.findBySocialProviderAndSocialId("google", "google-sub-123"))
                .willReturn(Optional.empty());

        User newUser = User.builder()
                .id(1L)
                .socialProvider("google")
                .socialId("google-sub-123")
                .email("test@gmail.com")
                .nickname("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        given(userRepository.save(any())).willReturn(newUser);
        given(jwtProvider.generateAccessToken(1L)).willReturn("access-token");
        given(refreshTokenRepository.save(any())).willAnswer(i -> i.getArgument(0));

        TokenResponse response = authService.login("valid-id-token");

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.userId()).isEqualTo(1L);
        verify(userRepository).save(any());
    }

    @Test
    void login_기존_유저_신규_가입_없이_토큰_반환() {
        given(googleTokenVerifier.verify("valid-id-token"))
                .willReturn(new GoogleUserInfo("google-sub-123", "test@gmail.com"));

        User existingUser = User.builder()
                .id(2L)
                .socialProvider("google")
                .socialId("google-sub-123")
                .email("test@gmail.com")
                .nickname("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        given(userRepository.findBySocialProviderAndSocialId("google", "google-sub-123"))
                .willReturn(Optional.of(existingUser));
        given(jwtProvider.generateAccessToken(2L)).willReturn("access-token");
        given(refreshTokenRepository.save(any())).willAnswer(i -> i.getArgument(0));

        TokenResponse response = authService.login("valid-id-token");

        assertThat(response.userId()).isEqualTo(2L);
        verify(userRepository).findBySocialProviderAndSocialId("google", "google-sub-123");
    }

    @Test
    void refresh_유효한_토큰_새_액세스_토큰_반환() {
        RefreshToken saved = RefreshToken.builder()
                .id(1L)
                .userId(1L)
                .token("valid-refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();
        given(refreshTokenRepository.findByToken("valid-refresh-token"))
                .willReturn(Optional.of(saved));
        given(jwtProvider.generateAccessToken(1L)).willReturn("new-access-token");

        TokenResponse response = authService.refresh("valid-refresh-token");

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("valid-refresh-token");
    }

    @Test
    void refresh_만료된_토큰_401_예외() {
        RefreshToken expired = RefreshToken.builder()
                .id(1L)
                .userId(1L)
                .token("expired-refresh-token")
                .expiresAt(LocalDateTime.now().minusDays(1))
                .createdAt(LocalDateTime.now())
                .build();
        given(refreshTokenRepository.findByToken("expired-refresh-token"))
                .willReturn(Optional.of(expired));

        assertThatThrownBy(() -> authService.refresh("expired-refresh-token"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void refresh_존재하지_않는_토큰_401_예외() {
        given(refreshTokenRepository.findByToken("unknown-token"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("unknown-token"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void logout_refresh_토큰_삭제() {
        RefreshToken saved = RefreshToken.builder()
                .id(1L)
                .userId(1L)
                .token("valid-refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();
        given(refreshTokenRepository.findByToken("valid-refresh-token"))
                .willReturn(Optional.of(saved));

        authService.logout("valid-refresh-token");

        verify(refreshTokenRepository).delete(saved);
    }
}
