package com.capstone.interviewtracker.constants.messages;

public final class CandidateMessages {

    public static final String CANDIDATE_CREATED = "Candidate application submitted successfully";

    public static final String CANDIDATE_ADVANCED = "Candidate advanced to next stage";

    public static final String CANDIDATE_REJECTED = "Candidate rejected";

    public static final String CANDIDATE_NOT_FOUND = "Candidate not found";

    public static final String CANDIDATE_ALREADY_SELECTED = "Candidate already selected";

    public static final String CANDIDATE_ALREADY_REJECTED = "Candidate already rejected";

    public static final String REAPPLICATION_ALLOWED_ONLY_AFTER_REJECTION = "Re-application allowed only after rejection.";

    public static final String CANNOT_ADVANCE_REJECTED_CANDIDATE = "Cannot advance rejected candidate";

    public static final String ACTIVE_APPLICATION_EXISTS = "Active application already exists.";

    public static final String ACTIVE_APPLICATION_PHONE_EXISTS = "An active application already exists for this phone. You can re-apply only after rejection.";

    private CandidateMessages() {
    }
}