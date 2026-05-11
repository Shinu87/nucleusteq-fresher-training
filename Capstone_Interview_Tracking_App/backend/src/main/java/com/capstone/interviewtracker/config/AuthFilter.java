package com.capstone.interviewtracker.config;

import com.capstone.interviewtracker.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Custom Basic Authentication Filter.
 */
@Component
public class AuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthFilter(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Skip authentication filter for auth endpoints.
     * These endpoints handle login/register logic separately.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Basic ")) {
            try {
                String base64 = authHeader.substring(6);
                String decoded = new String(
                        Base64.getDecoder().decode(base64),
                        StandardCharsets.UTF_8);

                int colonIdx = decoded.indexOf(':');
                if (colonIdx > 0) {

                    String email = decoded.substring(0, colonIdx);
                    String rawPassword = decoded.substring(colonIdx + 1);

                    userRepository.findByEmail(email).ifPresent(user -> {

                        if (user.isEnabled()
                                && user.getPassword() != null
                                && passwordEncoder.matches(rawPassword, user.getPassword())) {

                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    null,
                                    List.of(new SimpleGrantedAuthority(
                                            "ROLE_" + user.getRole().name())));

                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    });
                }

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}