package com.capstone.interviewtracker.dto.Response;

import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.enums.InterviewStatus;
import com.capstone.interviewtracker.enums.Stage;

import java.time.LocalDateTime;
import java.util.List;

public class ApplicationStatusDTO {

    private Long candidateId;
    private String candidateName;
    private String candidateEmail;

    private Long jobId;
    private String jobTitle;

    private Stage currentStage;
    private CandidateStatus applicationStatus;

    private String derivedStatus;

    /**
     * True if candidate is NOT rejected
     * (means they cannot apply again)
     */
    private boolean isLocked;

    private boolean resumeUploaded;

    private List<InterviewSummary> interviews;

    /* Used to store data of a single interview */
    public static class InterviewSummary {
        private Long interviewId;
        private Stage stage;
        private LocalDateTime scheduledAt;
        private List<String> panelNames;
        private InterviewStatus interviewStatus;
        private boolean feedbackSubmitted;

        public Long getInterviewId() {
            return interviewId;
        }

        public void setInterviewId(Long interviewId) {
            this.interviewId = interviewId;
        }

        public Stage getStage() {
            return stage;
        }

        public void setStage(Stage stage) {
            this.stage = stage;
        }

        public LocalDateTime getScheduledAt() {
            return scheduledAt;
        }

        public void setScheduledAt(LocalDateTime scheduledAt) {
            this.scheduledAt = scheduledAt;
        }

        public List<String> getPanelNames() {
            return panelNames;
        }

        public void setPanelNames(List<String> panelNames) {
            this.panelNames = panelNames;
        }

        public InterviewStatus getInterviewStatus() {
            return interviewStatus;
        }

        public void setInterviewStatus(InterviewStatus interviewStatus) {
            this.interviewStatus = interviewStatus;
        }

        public boolean isFeedbackSubmitted() {
            return feedbackSubmitted;
        }

        public void setFeedbackSubmitted(boolean feedbackSubmitted) {
            this.feedbackSubmitted = feedbackSubmitted;
        }
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public CandidateStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(CandidateStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getDerivedStatus() {
        return derivedStatus;
    }

    public void setDerivedStatus(String derivedStatus) {
        this.derivedStatus = derivedStatus;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isResumeUploaded() {
        return resumeUploaded;
    }

    public void setResumeUploaded(boolean resumeUploaded) {
        this.resumeUploaded = resumeUploaded;
    }

    public List<InterviewSummary> getInterviews() {
        return interviews;
    }

    public void setInterviews(List<InterviewSummary> interviews) {
        this.interviews = interviews;
    }
}
