package com.capstone.interviewtracker.constants.api;

/**
 * API endpoint constants for candidate-related features.
 *
 */
public final class CandidateApiConstants {

    /**
     * Base path for candidate APIs.
     */
    public static final String BASE_PATH = ApiConstants.API_BASE + "/candidates";

    /**
     * Endpoint for logged-in candidate's application details.
     */
    public static final String ME_APPLICATION = "/me/application";

    /**
     * Endpoint to fetch candidate by id.
     */
    public static final String BY_ID = "/{id}";

    /**
     * Endpoint to upload or update resume.
     */
    public static final String RESUME = "/{id}/resume";

    /**
     * Endpoint for re-applying after rejection.
     */
    public static final String REAPPLY = "/{id}/reapply";

    /**
     * Endpoint to move candidate to next stage (HR action).
     */
    public static final String ADVANCE = "/{id}/advance";

    /**
     * Endpoint to reject a candidate (HR action).
     */
    public static final String REJECT = "/{id}/reject";

    /**
     * Security pattern for all candidate APIs.
     */
    public static final String PATTERN = BASE_PATH + "/**";

    /**
     * Base path for resume-related APIs.
     */
    public static final String RESUMES_BASE_PATH = ApiConstants.API_BASE + "/resumes";

    /**
     * Endpoint to download resume file.
     */
    public static final String RESUMES_DOWNLOAD = "/download";

    /**
     * Security pattern for resume APIs.
     */
    public static final String RESUMES_PATTERN = RESUMES_BASE_PATH + "/**";

    /**
     * Private constructor to prevent object creation.
     */
    private CandidateApiConstants() {
    }
}