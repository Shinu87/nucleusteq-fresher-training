package com.capstone.interviewtracker.controller;

import com.capstone.interviewtracker.config.TestSecurityConfig;
import com.capstone.interviewtracker.dto.Request.LoginRequest;
import com.capstone.interviewtracker.dto.Request.SetPasswordRequest;
import com.capstone.interviewtracker.dto.Request.SignupRequest;
import com.capstone.interviewtracker.dto.Response.AuthResponse;
import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.exception.advice.CustomGlobalExceptionHandler;
import com.capstone.interviewtracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for AuthController.
 */
@WebMvcTest(controllers = AuthController.class)
@ContextConfiguration(classes = {
                AuthController.class,
                TestSecurityConfig.class,
                CustomGlobalExceptionHandler.class
})
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private UserService userService;

        /* signup */

        @Test
        void signup_validRequest_returnsOkWithAuthResponse() throws Exception {
                SignupRequest req = new SignupRequest();
                req.setName("Jane Doe");
                req.setEmail("jane@example.com");
                req.setMobile("9876543210");
                req.setGender("FEMALE");
                req.setDateOfBirth(LocalDate.of(2000, 1, 1));
                req.setRole(Role.CANDIDATE);

                AuthResponse resp = new AuthResponse(1L, "Jane Doe", "jane@example.com",
                                Role.CANDIDATE, "Account created successfully");
                when(userService.signup(any(SignupRequest.class))).thenReturn(resp);

                mockMvc.perform(post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.email").value("jane@example.com"))
                                .andExpect(jsonPath("$.role").value("CANDIDATE"))
                                .andExpect(jsonPath("$.message").value("Account created successfully"));
        }

        @Test
        void signup_invalidEmail_returnsBadRequest() throws Exception {
                SignupRequest req = new SignupRequest();
                req.setName("Jane");
                req.setEmail("not-an-email");
                req.setMobile("9876543210");
                req.setGender("FEMALE");
                req.setDateOfBirth(LocalDate.of(2000, 1, 1));
                req.setRole(Role.CANDIDATE);

                mockMvc.perform(post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void signup_blankName_returnsBadRequest() throws Exception {
                SignupRequest req = new SignupRequest();
                req.setName(""); // fails @NotBlank
                req.setEmail("jane@example.com");
                req.setMobile("9876543210");
                req.setGender("FEMALE");
                req.setDateOfBirth(LocalDate.of(2000, 1, 1));
                req.setRole(Role.CANDIDATE);

                mockMvc.perform(post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void signup_invalidMobilePattern_returnsBadRequest() throws Exception {
                SignupRequest req = new SignupRequest();
                req.setName("Jane");
                req.setEmail("jane@example.com");
                req.setMobile("12345");
                req.setGender("FEMALE");
                req.setDateOfBirth(LocalDate.of(2000, 1, 1));
                req.setRole(Role.CANDIDATE);

                mockMvc.perform(post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void signup_futureDateOfBirth_returnsBadRequest() throws Exception {
                SignupRequest req = new SignupRequest();
                req.setName("Jane");
                req.setEmail("jane@example.com");
                req.setMobile("9876543210");
                req.setGender("FEMALE");
                req.setDateOfBirth(LocalDate.now().plusDays(1));
                req.setRole(Role.CANDIDATE);

                mockMvc.perform(post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        /* login */

        @Test
        void login_validRequest_returnsAuthResponse() throws Exception {
                LoginRequest req = new LoginRequest();
                req.setEmail("hr@company.com");
                req.setPassword("Hr@12345");

                AuthResponse resp = new AuthResponse(10L, "Admin HR", "hr@company.com",
                                Role.HR, "Login successful");
                when(userService.login(any(LoginRequest.class))).thenReturn(resp);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(10))
                                .andExpect(jsonPath("$.email").value("hr@company.com"))
                                .andExpect(jsonPath("$.role").value("HR"))
                                .andExpect(jsonPath("$.message").value("Login successful"));
        }

        @Test
        void login_blankPassword_returnsBadRequest() throws Exception {
                LoginRequest req = new LoginRequest();
                req.setEmail("hr@company.com");
                req.setPassword("");
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void login_invalidEmail_returnsBadRequest() throws Exception {
                LoginRequest req = new LoginRequest();
                req.setEmail("invalid-email");
                req.setPassword("Hr@12345");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        /* set-password */

        @Test
        void setPassword_validRequest_returnsAuthResponse() throws Exception {
                SetPasswordRequest req = new SetPasswordRequest();
                req.setToken("token-abc-123");
                req.setPassword("NewPass@123");

                AuthResponse resp = new AuthResponse(5L, "Panel User", "panel@company.com",
                                Role.PANEL, "Password set successfully");
                when(userService.setPassword(any(SetPasswordRequest.class))).thenReturn(resp);

                mockMvc.perform(post("/api/auth/set-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(5))
                                .andExpect(jsonPath("$.role").value("PANEL"))
                                .andExpect(jsonPath("$.message").value("Password set successfully"));
        }

        @Test
        void setPassword_blankToken_returnsBadRequest() throws Exception {
                SetPasswordRequest req = new SetPasswordRequest();
                req.setToken("");
                req.setPassword("NewPass@123");

                mockMvc.perform(post("/api/auth/set-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void setPassword_shortPassword_returnsBadRequest() throws Exception {
                SetPasswordRequest req = new SetPasswordRequest();
                req.setToken("token-abc-123");
                req.setPassword("123");

                mockMvc.perform(post("/api/auth/set-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest());
        }
}