package com.capstone.interviewtracker.constants.messages;

public final class ValidationMessages {

    public static final String AGE_REQUIRED = "Age is required to apply.";
    public static final String AGE_MIN = "Minimum age is 18.";
    public static final String AGE_MAX = "Maximum age is 60.";

    public static final String VALUE_CANNOT_BE_NEGATIVE = "Value cannot be negative.";

    public static final String MIN_CANNOT_BE_GREATER_THAN_MAX = "Minimum value cannot be greater than maximum value.";

    public static final String EXPERIENCE_VALUES_CANNOT_BE_NEGATIVE = "Experience values cannot be negative.";

    public static final String SALARY_VALUES_CANNOT_BE_NEGATIVE = "Salary values cannot be negative.";

    public static final String SKILL_SELECTION_REQUIRED = "Please select at least one required skill.";

    public static final String RESUME_FILE_REQUIRED = "Resume file is empty or not provided";

    public static final String INVALID_RESUME_FILE_TYPE = "Only PDF files are allowed for resume upload.";

    public static final String INVALID_RESUME_EXTENSION = "Resume file must have a .pdf extension";

    public static final String INVALID_FILENAME_DETECTED = "Invalid filename: path traversal detected";

    public static final String RESUME_UPLOAD_FAILED = "Failed to upload resume file";

    public static final String RESUME_DIRECTORY_CREATION_FAILED = "Could not create resume upload directory";

    private ValidationMessages() {
    }
}