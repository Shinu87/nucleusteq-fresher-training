package com.capstone.interviewtracker.service.impl;

import com.capstone.interviewtracker.constants.messages.AuthMessages;
import com.capstone.interviewtracker.dto.Request.LoginRequest;
import com.capstone.interviewtracker.dto.Request.SetPasswordRequest;
import com.capstone.interviewtracker.dto.Request.SignupRequest;
import com.capstone.interviewtracker.dto.Response.AuthResponse;
import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.exception.custom.ConflictException;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.exception.custom.UnauthorizedException;
import com.capstone.interviewtracker.exception.custom.BadRequestException;
import com.capstone.interviewtracker.model.PasswordSetupToken;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.PasswordSetupTokenRepository;
import com.capstone.interviewtracker.repository.UserRepository;
import com.capstone.interviewtracker.service.EmailService;
import com.capstone.interviewtracker.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This class contains the logic for signup and login.
 */
@Service
public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final PasswordSetupTokenRepository tokenRepository;
        private final EmailService emailService;

        @Value("${app.frontend.url:http://127.0.0.1:5500/Capstone_Interview_Tracking_App/frontend}")
        private String frontendBaseUrl;

        /**
         * Constructs UserServiceImpl with required dependencies.
         *
         * @param userRepository  user repository for DB operations
         * @param passwordEncoder encoder used for hashing passwords
         * @param tokenRepository repository for password setup tokens
         * @param emailService    service used for sending emails
         */
        public UserServiceImpl(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        PasswordSetupTokenRepository tokenRepository,
                        EmailService emailService) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.tokenRepository = tokenRepository;
                this.emailService = emailService;
        }

        /**
         * Registers a new user in the system.
         * Creates a user account in disabled state and sends a password setup link via
         * email.
         *
         * @param request signup request containing user details
         * @return authentication response after successful registration
         * @throws RuntimeException if email already exists
         */
        @Override
        public AuthResponse signup(SignupRequest request) {

                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new ConflictException(AuthMessages.EMAIL_ALREADY_EXISTS);
                }

                User user = new User();
                user.setName(request.getName().trim());
                user.setEmail(request.getEmail().trim().toLowerCase());
                user.setMobile(request.getMobile());
                user.setGender(request.getGender());
                user.setAge(request.getAge());
                user.setRole(request.getRole() != null ? request.getRole() : Role.CANDIDATE);
                user.setPassword(null);
                user.setEnabled(false);

                User savedUser = userRepository.save(user);

                String setLink = createTokenAndBuildLink(
                                savedUser.getEmail(),
                                savedUser.getRole().name());

                emailService.sendCandidateSignupEmail(
                                savedUser.getEmail(),
                                savedUser.getName(),
                                setLink);

                return new AuthResponse(
                                savedUser.getId(),
                                savedUser.getName(),
                                savedUser.getEmail(),
                                savedUser.getRole(),
                                "Signup successful. Please check your email to set your password.",
                                savedUser.getAge());
        }

        /**
         * Authenticates a user by validating email and password.
         * Ensures the user has set a password and the account is enabled.
         *
         * @param request login request containing email and password
         * @return authentication response on successful login
         * @throws ResourceNotFoundException   if user is not found
         * @throws InvalidCredentialsException if password is missing, incorrect, or
         *                                     account is disabled
         */
        @Override
        public AuthResponse login(LoginRequest request) {

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                AuthMessages.USER_NOT_FOUND));

                if (user.getPassword() == null || user.getPassword().isBlank()) {
                        throw new BadRequestException(
                                        AuthMessages.SET_PASSWORD_LINK_REQUIRED);
                }

                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        throw new BadRequestException(
                                        AuthMessages.INVALID_PASSWORD);
                }

                if (!user.isEnabled()) {
                        throw new UnauthorizedException(
                                        AuthMessages.ACCOUNT_DEACTIVATED);
                }

                return new AuthResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole(),
                                "Login successful",
                                user.getAge());
        }

        /**
         * Sets a new password for the user using a valid password setup token.
         * Marks the token as used after successful password creation and enables the
         * user account.
         *
         * @param request request containing token and new password
         * @return authentication response confirming password setup
         * @throws RuntimeException          if token is invalid, expired, or already
         *                                   used
         * @throws ResourceNotFoundException if no user is found for the token
         */
        @Override
        public AuthResponse setPassword(SetPasswordRequest request) {

                PasswordSetupToken tokenRow = tokenRepository.findByToken(request.getToken())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                AuthMessages.INVALID_OR_UNKNOWN_PASSWORD_LINK));

                if (tokenRow.isUsed()) {
                        throw new BadRequestException(
                                        AuthMessages.PASSWORD_LINK_ALREADY_USED);
                }

                if (tokenRow.getExpiresAt().isBefore(LocalDateTime.now())) {
                        throw new BadRequestException(
                                        AuthMessages.PASSWORD_LINK_EXPIRED);
                }

                User user = userRepository.findByEmail(tokenRow.getEmail())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                AuthMessages.NO_USER_FOR_TOKEN));

                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setEnabled(true);
                userRepository.save(user);

                tokenRow.setUsed(true);
                tokenRepository.save(tokenRow);

                return new AuthResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole(),
                                "Password set successfully. You can now log in.");
        }

        /**
         * Creates a password setup token for a user and builds a frontend link.
         * The token is valid for 24 hours and is stored in the database.
         *
         * @param email user email for which the token is generated
         * @param role  role of the user (CANDIDATE, PANEL, HR, etc.)
         * @return frontend password setup URL containing the generated token
         */
        public String createTokenAndBuildLink(String email, String role) {

                String token = UUID.randomUUID().toString().replace("-", "");
                LocalDateTime now = LocalDateTime.now();

                PasswordSetupToken row = new PasswordSetupToken(
                                token,
                                email,
                                role,
                                now,
                                now.plusHours(24));

                tokenRepository.save(row);

                return frontendBaseUrl + "/set-password.html?token=" + token;
        }
}