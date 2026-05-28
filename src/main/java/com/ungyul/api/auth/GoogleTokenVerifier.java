package com.ungyul.api.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GoogleTokenVerifier {

    private final WebClient webClient;
    private final String googleClientId;

    public GoogleTokenVerifier(
            WebClient.Builder webClientBuilder,
            @Value("${auth.google.client-id}") String googleClientId) {
        this.webClient = webClientBuilder.baseUrl("https://oauth2.googleapis.com").build();
        this.googleClientId = googleClientId;
    }

    public GoogleUserInfo verify(String idToken) {
        GoogleTokenInfo tokenInfo = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tokeninfo")
                        .queryParam("id_token", idToken)
                        .build())
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), resp ->
                        resp.bodyToMono(String.class).map(body ->
                                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Google 토큰")))
                .bodyToMono(GoogleTokenInfo.class)
                .block();

        if (tokenInfo == null || !googleClientId.equals(tokenInfo.aud())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google 클라이언트 ID 불일치");
        }

        return new GoogleUserInfo(tokenInfo.sub(), tokenInfo.email());
    }

    record GoogleTokenInfo(String sub, String email, String aud) {}

    record GoogleUserInfo(String socialId, String email) {}
}
