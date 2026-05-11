package com.capstone.interviewtracker.constants.api;

/**
 * API endpoint constants for HR-related operations.
 *
 */
public final class HrApiConstants {

    /* Jobs APIs */

    /**
     * Base path for job APIs.
     */
    public static final String JOBS_BASE_PATH = ApiConstants.API_BASE + "/jobs";

    /**
     * Endpoint to fetch active jobs.
     */
    public static final String JOBS_ACTIVE = "/active";

    /**
     * Endpoint to activate a job.
     */
    public static final String JOBS_ACTIVATE = "/{id}/activate";

    /**
     * Endpoint to deactivate a job.
     */
    public static final String JOBS_DEACTIVATE = "/{id}/deactivate";

    /**
     * Security pattern for job APIs.
     */
    public static final String JOBS_PATTERN = JOBS_BASE_PATH + "/**";

    /* Panel APIs */

    /**
     * Base path for panel APIs.
     */
    public static final String PANELS_BASE_PATH = ApiConstants.API_BASE + "/panels";

    /**
     * Endpoint to fetch panel member by id.
     */
    public static final String PANELS_BY_ID = "/{id}";

    /**
     * Endpoint to activate a panel member.
     */
    public static final String PANELS_ACTIVATE = "/{id}/activate";

    /**
     * Security pattern for panel APIs.
     */
    public static final String PANELS_PATTERN = PANELS_BASE_PATH + "/**";

    /* Interview APIs */
    /**
     * Base path for interview APIs.
     */
    public static final String INTERVIEWS_BASE_PATH = ApiConstants.API_BASE + "/interviews";

    /**
     * Endpoint to fetch interview by id.
     */
    public static final String INTERVIEWS_BY_ID = "/{id}";

    /**
     * Endpoint to mark interview as completed.
     */
    public static final String INTERVIEWS_COMPLETE = "/{id}/complete";

    /**
     * Endpoint to fetch interviews by candidate id.
     */
    public static final String INTERVIEWS_BY_CANDIDATE = "/candidate/{candidateId}";

    /**
     * Security pattern for interview APIs.
     */
    public static final String INTERVIEWS_PATTERN = INTERVIEWS_BASE_PATH + "/**";

    /* Skills APIs */

    /**
     * Base path for skill APIs.
     */
    public static final String SKILLS_BASE_PATH = ApiConstants.API_BASE + "/skills";

    /**
     * Security pattern for skill APIs.
     */
    public static final String SKILLS_PATTERN = SKILLS_BASE_PATH + "/**";

    /**
     * Private constructor to prevent instantiation.
     */
    private HrApiConstants() {
    }
}