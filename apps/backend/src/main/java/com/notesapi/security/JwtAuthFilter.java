package com.notesapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private static final Set<String> EXCLUDED_PATH_PREFIXES = Set.of(
        "/auth/",
        "/ws/",
        "/actuator/",
        "/error",
        "/favicon.ico"
    );

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        final String uri = request.getRequestURI();
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
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String bearer = resolveBearerToken(request);

                if (bearer != null && isLikelyJwt(bearer)) {
                    String subject = validateAndGetSubject(bearer);

                    if (subject != null && !subject.isBlank()) {
                        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(subject, null, authorities);

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        Long numericId = parseLongSafely(subject);

                        if (numericId != null) {
                            request.setAttribute("discordUserId", numericId);
                        }
                    }
                }
            }
        } catch (ExpiredJwtException e) {
            log.debug("JWT expired: {}", e.getMessage());
        } catch (JwtException e) {
            log.debug("JWT invalid: {}", e.getMessage());
        } catch (Exception e) {
            log.debug("JwtAuthFilter encountered an error: {}", e.getMessage());
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

    private boolean isLikelyJwt(String token) {
        int dotCount = 0;

        for (int i = 0; i < token.length(); i++) {
            if (token.charAt(i) == '.') dotCount++;
            if (dotCount > 2) break;
        }

        return dotCount == 2;
    }

    private String validateAndGetSubject(String token) {
        var parser = Jwts.parserBuilder()
            .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
            .build();

        var claimsJws = parser.parseClaimsJws(token);

        return claimsJws.getBody().getSubject();
    }

    private Long parseLongSafely(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
