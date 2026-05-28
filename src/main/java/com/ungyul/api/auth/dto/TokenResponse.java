package com.ungyul.api.auth.dto;

public record TokenResponse(String accessToken, String refreshToken, Long userId) {}
