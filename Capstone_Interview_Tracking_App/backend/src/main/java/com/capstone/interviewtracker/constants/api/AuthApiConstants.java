package com.capstone.interviewtracker.constants.api;

/**
 * API endpoint constants for authentication related operations.
 *
 */
public final class AuthApiConstants {

    /**
     * Base path for all authentication APIs.
     */
    public static final String BASE_PATH = ApiConstants.API_BASE + "/auth";

    /**
     * Endpoint for user signup.
     */
    public static final String SIGNUP = "/signup";

    /**
     * Endpoint for user login.
     */
    public static final String LOGIN = "/login";

    /**
     * Endpoint for setting password using a secure token.
     */
    public static final String SET_PASSWORD = "/set-password";

    /**
     * Security pattern used to allow all auth-related endpoints.
     */
    public static final String PATTERN = BASE_PATH + "/**";

    /**
     * Private constructor to prevent object creation.
     */
    private AuthApiConstants() {
    }
}