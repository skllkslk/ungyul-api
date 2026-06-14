package com.ungyul.api.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(
                "test-secret-key-must-be-at-least-32-bytes!",
                3_600_000L
        );
    }

    @Test
    void 토큰_생성_후_userId_추출_성공() {
        String token = jwtProvider.generateAccessToken(42L);

        Long userId = jwtProvider.getUserId(token);

        assertThat(userId).isEqualTo(42L);
    }

    @Test
    void 유효한_토큰_검증_성공() {
        String token = jwtProvider.generateAccessToken(1L);

        assertThat(jwtProvider.validate(token)).isTrue();
    }

    @Test
    void 만료된_토큰_검증_실패() {
        JwtProvider expiredProvider = new JwtProvider(
                "test-secret-key-must-be-at-least-32-bytes!",
                -1L
        );
        String token = expiredProvider.generateAccessToken(1L);

        assertThat(jwtProvider.validate(token)).isFalse();
    }

    @Test
    void 위조된_토큰_검증_실패() {
        String token = jwtProvider.generateAccessToken(1L);
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(jwtProvider.validate(tampered)).isFalse();
    }

    @Test
    void 빈_문자열_토큰_검증_실패() {
        assertThat(jwtProvider.validate("")).isFalse();
    }
}
