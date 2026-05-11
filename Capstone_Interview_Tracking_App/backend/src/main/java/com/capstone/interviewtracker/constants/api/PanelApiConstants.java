package com.capstone.interviewtracker.constants.api;

/**
 * API endpoint constants for panel-related operations.
 *
 */
public final class PanelApiConstants {

    /* Feedback APIs */

    /**
     * Base path for feedback APIs.
     */
    public static final String BASE_PATH = ApiConstants.API_BASE + "/feedbacks";

    /**
     * Endpoint to check if feedback already exists for a panel.
     */
    public static final String CHECK = "/check";

    /**
     * Endpoint to get feedback by interview id.
     */
    public static final String BY_INTERVIEW = "/interview/{interviewId}";

    /**
     * Endpoint to get feedback by candidate id.
     */
    public static final String BY_CANDIDATE = "/candidate/{candidateId}";

    /**
     * Security pattern for feedback APIs.
     */
    public static final String PATTERN = BASE_PATH + "/**";

    /**
     * Private constructor to prevent object creation.
     */
    private PanelApiConstants() {
    }
}