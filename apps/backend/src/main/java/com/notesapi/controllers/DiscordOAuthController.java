package com.notesapi.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpStatusCode;

import java.util.Map;
import com.notesapi.security.JwtService;

@RestController
@RequestMapping("/auth")
public class DiscordOAuthController {
    @Value("${discord.client_id}")
    private String clientId;

    @Value("${discord.client_secret}")
    private String clientSecret;

    @Value("${discord.redirect_uri}")
    private String redirectUri;

    private final JwtService jwtService;

    public DiscordOAuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/exchange")
    public ResponseEntity<?> exchangeCode(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        RestTemplate restTemplate = new RestTemplate();

        String tokenUrl = "https://discord.com/api/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);

        String redirectFromRequest = body.get("redirect_uri");
        String redirectToUse = (redirectFromRequest != null && !redirectFromRequest.isBlank()) ? redirectFromRequest : redirectUri;

        formData.add("redirect_uri", redirectToUse);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<Map> tokenResponse;

        try {
            tokenResponse = restTemplate.postForEntity(tokenUrl, request, Map.class);
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String responseBody = e.getResponseBodyAsString();

            return ResponseEntity.status(status)
                .body(Map.of("error", "Token exchange failed", "status", status.value(), "details", responseBody));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Unexpected error during token exchange", "details", e.getMessage()));
        }

        if (tokenResponse == null || !tokenResponse.getStatusCode().is2xxSuccessful() || tokenResponse.getBody() == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Invalid token response", "raw", tokenResponse));
        }

        Object accessObj = tokenResponse.getBody().get("access_token");

        if (accessObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "access_token missing from token response", "raw", tokenResponse.getBody()));
        }

        String accessToken = accessObj.toString();

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);

        ResponseEntity<Map> userResponse;

        try {
            userResponse = restTemplate.exchange(
                "https://discord.com/api/users/@me",
                HttpMethod.GET,
                new HttpEntity<>(userHeaders),
                Map.class
            );
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String responseBody = e.getResponseBodyAsString();

            return ResponseEntity.status(status)
                .body(Map.of("error", "Failed to fetch user info", "status", status.value(), "details", responseBody));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Unexpected error fetching user info", "details", e.getMessage()));
        }

        Object userIdObj = userResponse.getBody() != null ? userResponse.getBody().get("id") : null;
        String discordUserId = userIdObj != null ? userIdObj.toString() : null;

        if (discordUserId == null || discordUserId.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Missing id in Discord user info"));
        }

        String jwt = jwtService.generate(discordUserId);

        return ResponseEntity.ok(Map.of(
            "token", jwt,
            "provider_token", accessToken,
            "user", userResponse.getBody()
        ));
    }
}
