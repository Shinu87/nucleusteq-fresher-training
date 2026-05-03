package com.capstone.interviewtracker.constants.messages;

public final class AuthMessages {

    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String SIGNUP_SUCCESS = "Account created successfully";
    public static final String PASSWORD_SET_SUCCESS = "Password set successfully";

    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String INVALID_PASSWORD = "Invalid password";

    public static final String ACCOUNT_DEACTIVATED = "Your account has been deactivated. Please contact HR.";

    public static final String PASSWORD_NOT_SET = "Please set your password before logging in.";

    public static final String PASSWORD_LINK_INVALID = "Invalid or unknown password setup link.";

    public static final String PASSWORD_LINK_EXPIRED = "This password setup link has expired. Please request a new one.";

    public static final String USER_NOT_FOUND = "User not found";

    public static final String SET_PASSWORD_LINK_REQUIRED = "Please set your password using the link sent to your email before logging in.";

    public static final String INVALID_OR_UNKNOWN_PASSWORD_LINK = "Invalid or unknown password setup link.";

    public static final String PASSWORD_LINK_ALREADY_USED = "This password setup link has already been used.";

    public static final String NO_USER_FOR_TOKEN = "No user found for this token.";

    private AuthMessages() {
    }
}