package com.capstone.interviewtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthFilter authFilter;

    public SecurityConfig(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    /**
     * Main security filter chain configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        /* Auth-related endpoints (login/signup/reset password) */
                        .requestMatchers("/api/auth/**").permitAll()

                        /* Public job listing (homepage access without login) */
                        .requestMatchers(HttpMethod.GET, "/api/jobs/active").permitAll()

                        /* Resume upload during signup flow */
                        .requestMatchers(HttpMethod.POST, "/api/candidates/*/resume")
                        .hasAnyRole("HR", "CANDIDATE")

                        /* Resume upload download */
                        .requestMatchers(HttpMethod.GET, "/api/resumes/download")
                        .hasAnyRole("HR", "PANEL", "CANDIDATE")

                        /* Test endpoints */
                        .requestMatchers("/test/**").permitAll()

                        /* Everything else requires authentication */
                        .anyRequest().authenticated())

                /* Add custom authentication filter before Spring’s default filter */
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}