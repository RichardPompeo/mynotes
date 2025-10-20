package com.notesapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DiscordAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(DiscordAuthFilter.class);

    private static final Set<String> EXCLUDED_PATH_PREFIXES = Set.of(
        "/auth/",
        "/ws/",
        "/actuator/",
        "/error",
        "/favicon.ico"
    );

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    private static final long CACHE_TTL_SECONDS = 60;

    private final RestTemplate rest = new RestTemplate();

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        final String uri = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        for (String prefix : EXCLUDED_PATH_PREFIXES) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = resolveBearerToken(request);

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                DiscordUserInfo userInfo = validateTokenAndGetUser(token);

                if (userInfo != null && userInfo.id() != null) {
                    Collection<? extends GrantedAuthority> authorities = Collections.emptyList();

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userInfo.id(), null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    request.setAttribute("discordUserId", userInfo.numericId());
                }
            }
        } catch (Exception e) {
            log.debug("DiscordAuthFilter encountered an error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String resolveBearerToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null) return null;

        if (header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();

            return token.isEmpty() ? null : token;
        }

        return null;
    }

    private DiscordUserInfo validateTokenAndGetUser(String token) {
        CacheEntry cached = cache.get(token);

        if (cached != null && !cached.isExpired()) {
            return new DiscordUserInfo(cached.discordUserId);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> res = rest.exchange(
                "https://discord.com/api/users/@me",
                HttpMethod.GET,
                entity,
                Map.class
            );

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                log.debug("Discord user info call returned non-2xx or empty body: {}", res.getStatusCode());
                return null;
            }

            Object idObj = res.getBody().get("id");

            if (idObj == null) {
                log.debug("Discord user info missing 'id' field");
                return null;
            }

            String idStr = String.valueOf(idObj).trim();
            Long idLong = parseLongSafely(idStr);

            cache.put(token, new CacheEntry(idStr, Instant.now().plusSeconds(CACHE_TTL_SECONDS)));

            return new DiscordUserInfo(idStr, idLong);
        } catch (HttpClientErrorException e) {
            log.debug("Discord token validation failed with status {}: {}", e.getStatusCode(), e.getMessage());
            cache.remove(token);

            return null;
        } catch (Exception e) {
            log.debug("Unexpected error validating Discord token: {}", e.getMessage());

            return null;
        }
    }

    private Long parseLongSafely(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private record DiscordUserInfo(String id, Long numericId) {
        DiscordUserInfo(String id) {
            this(id, null);
        }
    }

    private static final class CacheEntry {
        private final String discordUserId;
        private final Instant expiresAt;

        CacheEntry(String discordUserId, Instant expiresAt) {
            this.discordUserId = discordUserId;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
