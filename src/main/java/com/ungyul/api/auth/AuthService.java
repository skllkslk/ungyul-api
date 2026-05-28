package com.ungyul.api.auth;

import com.ungyul.api.auth.GoogleTokenVerifier.GoogleUserInfo;
import com.ungyul.api.auth.dto.TokenResponse;
import com.ungyul.api.common.security.JwtProvider;
import com.ungyul.api.user.User;
import com.ungyul.api.user.UserRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Value("${auth.jwt.refresh-token-expiration-days}")
    private int refreshTokenExpirationDays;

    @Transactional
    public TokenResponse login(String idToken) {
        GoogleUserInfo googleUser = googleTokenVerifier.verify(idToken);

        User user = userRepository.findBySocialProviderAndSocialId("google", googleUser.socialId())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .socialProvider("google")
                                .socialId(googleUser.socialId())
                                .email(googleUser.email())
                                .nickname(deriveNickname(googleUser.email()))
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()));

        return issueTokens(user.getId());
    }

    @Transactional
    public TokenResponse refresh(String refreshTokenValue) {
        RefreshToken saved = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token"));

        if (saved.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(saved);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 Refresh Token");
        }

        String newAccessToken = jwtProvider.generateAccessToken(saved.getUserId());
        return new TokenResponse(newAccessToken, refreshTokenValue, saved.getUserId());
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(refreshTokenRepository::delete);
    }

    private TokenResponse issueTokens(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);

        String accessToken = jwtProvider.generateAccessToken(userId);
        String refreshTokenValue = UUID.randomUUID().toString();

        refreshTokenRepository.save(RefreshToken.builder()
                .userId(userId)
                .token(refreshTokenValue)
                .expiresAt(LocalDateTime.now().plusDays(refreshTokenExpirationDays))
                .createdAt(LocalDateTime.now())
                .build());

        return new TokenResponse(accessToken, refreshTokenValue, userId);
    }

    private String deriveNickname(String email) {
        return email.split("@")[0];
    }
}
