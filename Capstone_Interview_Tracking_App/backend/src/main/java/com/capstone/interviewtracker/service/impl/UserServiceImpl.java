package com.capstone.interviewtracker.service.impl;

import java.util.Base64;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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

                logger.info("Signup attempt started for email: {}", request.getEmail());

                if (userRepository.existsByEmail(request.getEmail())) {
                        logger.warn("Signup failed - email already exists: {}", request.getEmail());
                        throw new ConflictException(AuthMessages.EMAIL_ALREADY_EXISTS);
                }

                logger.debug("Building new User object for email: {}", request.getEmail());

                User user = new User();
                user.setName(request.getName().trim());
                user.setEmail(request.getEmail().trim().toLowerCase());
                user.setMobile(request.getMobile());
                user.setGender(request.getGender());
                user.setDateOfBirth(request.getDateOfBirth());
                user.setRole(request.getRole() != null ? request.getRole() : Role.CANDIDATE);
                user.setPassword(null);
                user.setEnabled(false);

                logger.debug("Saving new user to database for email: {}", user.getEmail());
                User savedUser = userRepository.save(user);
                logger.info("User saved successfully with ID: {} and role: {}", savedUser.getId(), savedUser.getRole());

                logger.debug("Creating password setup token for email: {}", savedUser.getEmail());
                String setLink = createTokenAndBuildLink(
                                savedUser.getEmail(),
                                savedUser.getRole().name());

                logger.info("Sending signup email to: {}", savedUser.getEmail());
                emailService.sendCandidateSignupEmail(
                                savedUser.getEmail(),
                                savedUser.getName(),
                                setLink);
                logger.info("Signup email sent successfully to: {}", savedUser.getEmail());

                logger.info("Signup completed successfully for email: {}", savedUser.getEmail());

                return new AuthResponse(
                                savedUser.getId(),
                                savedUser.getName(),
                                savedUser.getEmail(),
                                savedUser.getRole(),
                                "Signup successful. Please check your email to set your password.",
                                savedUser.getDateOfBirth());
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

                logger.info("Login attempt started for email: {}", request.getEmail());

                logger.debug("Looking up user in database for email: {}", request.getEmail());
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> {
                                        logger.warn("Login failed - user not found for email: {}", request.getEmail());
                                        return new ResourceNotFoundException(AuthMessages.USER_NOT_FOUND);
                                });

                logger.debug("User found for email: {}, checking password setup", request.getEmail());

                if (user.getPassword() == null || user.getPassword().isBlank()) {
                        logger.warn("Login failed - password not set for email: {}", request.getEmail());
                        throw new BadRequestException(
                                        AuthMessages.SET_PASSWORD_LINK_REQUIRED);
                }

                if (!passwordEncoder.matches(
                                new String(java.util.Base64.getDecoder().decode(request.getPassword())),
                                user.getPassword())) {
                        logger.warn("Login failed - invalid password for email: {}", request.getEmail());
                        throw new BadRequestException(
                                        AuthMessages.INVALID_PASSWORD);
                }

                if (!user.isEnabled()) {
                        logger.warn("Login failed - account is deactivated for email: {}", request.getEmail());
                        throw new UnauthorizedException(
                                        AuthMessages.ACCOUNT_DEACTIVATED);
                }

                logger.info("Login successful for email: {} with role: {}", user.getEmail(), user.getRole());

                return new AuthResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole(),
                                "Login successful",
                                user.getDateOfBirth());
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

                logger.info("Set password request received for token: {}", request.getToken());

                logger.debug("Looking up password setup token in database");
                PasswordSetupToken tokenRow = tokenRepository.findByToken(request.getToken())
                                .orElseThrow(() -> {
                                        logger.warn("Set password failed - invalid or unknown token: {}",
                                                        request.getToken());
                                        return new ResourceNotFoundException(
                                                        AuthMessages.INVALID_OR_UNKNOWN_PASSWORD_LINK);
                                });

                logger.debug("Token found for email: {}, validating token status", tokenRow.getEmail());

                if (tokenRow.isUsed()) {
                        logger.warn("Set password failed - token already used for email: {}", tokenRow.getEmail());
                        throw new BadRequestException(
                                        AuthMessages.PASSWORD_LINK_ALREADY_USED);
                }

                if (tokenRow.getExpiresAt().isBefore(LocalDateTime.now())) {
                        logger.warn("Set password failed - token expired for email: {}", tokenRow.getEmail());
                        throw new BadRequestException(
                                        AuthMessages.PASSWORD_LINK_EXPIRED);
                }

                logger.debug("Fetching user from database for email: {}", tokenRow.getEmail());
                User user = userRepository.findByEmail(tokenRow.getEmail())
                                .orElseThrow(() -> {
                                        logger.error("Set password failed - no user found for token email: {}",
                                                        tokenRow.getEmail());
                                        return new ResourceNotFoundException(AuthMessages.NO_USER_FOR_TOKEN);
                                });

                logger.debug("Encoding and saving new password for user: {}", user.getEmail());
                String decodedPassword = new String(
                                Base64.getDecoder().decode(request.getPassword()));

                user.setPassword(passwordEncoder.encode(decodedPassword));
                user.setEnabled(true);
                userRepository.save(user);
                logger.info("Password updated and account enabled for user: {}", user.getEmail());

                logger.debug("Marking password setup token as used for email: {}", tokenRow.getEmail());
                tokenRow.setUsed(true);
                tokenRepository.save(tokenRow);
                logger.debug("Token marked as used successfully for email: {}", tokenRow.getEmail());

                logger.info("Password set successfully for user: {}", user.getEmail());

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

                logger.info("Generating password setup token for email: {} with role: {}", email, role);

                String token = UUID.randomUUID().toString().replace("-", "");
                LocalDateTime now = LocalDateTime.now();

                logger.debug("Saving password setup token to database for email: {}", email);
                PasswordSetupToken row = new PasswordSetupToken(
                                token,
                                email,
                                role,
                                now,
                                now.plusHours(24));

                tokenRepository.save(row);
                logger.debug("Password setup token saved successfully for email: {}, expires at: {}", email,
                                now.plusHours(24));

                logger.info("Password setup link generated for email: {}", email);

                return frontendBaseUrl + "/set-password.html?token=" + token;
        }
}