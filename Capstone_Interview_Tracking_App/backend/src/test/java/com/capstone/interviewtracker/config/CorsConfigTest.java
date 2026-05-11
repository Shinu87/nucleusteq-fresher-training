package com.capstone.interviewtracker.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for CorsConfig.
 */
class CorsConfigTest {

    /**
     * Tests that CORS mappings are added correctly with expected settings.
     */
    @Test
    void testAddCorsMappings() {
        CorsConfig config = new CorsConfig();

        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class);

        when(registry.addMapping("/**")).thenReturn(registration);

        when(registration.allowedOrigins(any(String[].class))).thenReturn(registration);
        when(registration.allowedMethods(any(String[].class))).thenReturn(registration);
        when(registration.allowedHeaders(any(String.class))).thenReturn(registration);
        when(registration.allowCredentials(any(Boolean.class))).thenReturn(registration);
        when(registration.exposedHeaders(any(String.class))).thenReturn(registration);

        config.addCorsMappings(registry);

        verify(registry).addMapping("/**");
        verify(registration).allowedOrigins(any(String[].class));
        verify(registration).allowedMethods(any(String[].class));
        verify(registration).allowedHeaders("*");
        verify(registration).allowCredentials(false);
        verify(registration).exposedHeaders("Authorization");
    }
}