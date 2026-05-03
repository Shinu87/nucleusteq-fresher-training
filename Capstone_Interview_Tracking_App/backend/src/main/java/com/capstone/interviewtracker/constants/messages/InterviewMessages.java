package com.capstone.interviewtracker.constants.messages;

public final class InterviewMessages {

    public static final String INTERVIEW_SCHEDULED = "Interview scheduled successfully";

    public static final String INTERVIEW_COMPLETED = "Interview marked as completed";

    public static final String INTERVIEW_NOT_FOUND = "Interview not found";

    public static final String INTERVIEW_ALREADY_EXISTS_FOR_STAGE = "Interview already exists for this stage.";

    public static final String INTERVIEW_ALREADY_COMPLETED = "Interview is already marked as COMPLETED.";

    public static final String CANNOT_COMPLETE_CANCELLED_INTERVIEW = "Cannot complete a cancelled interview.";

    public static final String CANNOT_SCHEDULE_INTERVIEW_FOR_REJECTED_CANDIDATE = "Cannot schedule interview for rejected candidate.";

    public static final String CANNOT_SCHEDULE_INTERVIEW_FOR_SELECTED_CANDIDATE = "Cannot schedule interview for selected candidate.";

    public static final String SCHEDULED_TIME_MUST_BE_IN_FUTURE = "Scheduled time must be in the future.";

    public static final String FEEDBACK_ONLY_AFTER_INTERVIEW_COMPLETION = "Feedback can only be submitted after interview completion time.";

    public static final String PANEL_NOT_ASSIGNED_TO_INTERVIEW = "Panel is not assigned to this interview.";

    public static final String FEEDBACK_ALREADY_SUBMITTED_BY_PANEL = "Feedback already submitted by this panel for this interview.";

    public static final String L1_INTERVIEW_NOT_SCHEDULED = "L1 interview not scheduled.";

    public static final String L2_INTERVIEW_NOT_SCHEDULED = "L2 interview not scheduled.";

    public static final String CANNOT_SCHEDULE_SCREENING_BEFORE_PROFILING_COMPLETION = "Cannot schedule SCREENING before PROFILING completion.";

    public static final String CANNOT_SCHEDULE_L1_BEFORE_SCREENING_COMPLETION = "Cannot schedule L1 before SCREENING completion.";

    public static final String L1_MUST_BE_COMPLETED_BEFORE_L2 = "L1 must be completed before L2.";

    public static final String L2_MUST_BE_COMPLETED_BEFORE_HR = "L2 must be completed before HR.";

    public static final String PANEL_ASSIGNMENT_MUST_BE_BETWEEN_1_AND_2 = "1 to 2 panels must be assigned.";

    public static final String HR_USER_NOT_FOUND_CANNOT_SCHEDULE_INTERVIEW = "HR user not found. Cannot schedule HR interview.";

    public static final String CONFIGURED_HR_ACCOUNT_NOT_IN_HR_ROLE = "Configured HR account is not in HR role.";

    public static final String ALL_PANEL_FEEDBACK_REQUIRED_BEFORE_STAGE_CHANGE = "All panel feedback required before stage change";

    public static final String INVALID_STAGE_TRANSITION = "Invalid stage transition";

    private InterviewMessages() {
    }
}