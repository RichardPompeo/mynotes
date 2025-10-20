package com.notesapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import com.notesapi.security.DiscordAuthFilter;
import com.notesapi.security.JwtAuthFilter;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final DiscordAuthFilter discordAuthFilter;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(DiscordAuthFilter discordAuthFilter, JwtAuthFilter jwtAuthFilter) {
        this.discordAuthFilter = discordAuthFilter;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    private static final List<String> ALLOWED_ORIGINS = List.of(
        "http://localhost:5173",
        "http://localhost:3000"
    );

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(ALLOWED_ORIGINS);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf()
            .disable()
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()
                .requestMatchers("/auth/**")
                .permitAll()
                .requestMatchers("/ws/**")
                .permitAll()
                .requestMatchers("/notes/**")
                .permitAll()
                .anyRequest()
                .authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(discordAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
