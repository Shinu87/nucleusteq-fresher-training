package com.capstone.interviewtracker.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.capstone.interviewtracker.exception.custom.BadRequestException;
import com.capstone.interviewtracker.exception.custom.ConflictException;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.exception.custom.UnauthorizedException;
import com.capstone.interviewtracker.constants.messages.CandidateMessages;
import com.capstone.interviewtracker.constants.messages.InterviewMessages;
import com.capstone.interviewtracker.constants.messages.JobMessages;
import com.capstone.interviewtracker.constants.messages.ValidationMessages;
import com.capstone.interviewtracker.dto.Request.CandidateRequestDTO;
import com.capstone.interviewtracker.dto.Response.ApplicationStatusDTO;
import com.capstone.interviewtracker.dto.Response.ApplicationStatusDTO.InterviewSummary;
import com.capstone.interviewtracker.dto.Response.CandidateResponseDTO;
import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.enums.Stage;
import com.capstone.interviewtracker.model.Candidate;
import com.capstone.interviewtracker.model.Interview;
import com.capstone.interviewtracker.model.JobDescription;
import com.capstone.interviewtracker.model.Panel;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.CandidateRepository;
import com.capstone.interviewtracker.repository.FeedbackRepository;
import com.capstone.interviewtracker.repository.InterviewRepository;
import com.capstone.interviewtracker.repository.JobDescriptionRepository;
import com.capstone.interviewtracker.repository.UserRepository;
import com.capstone.interviewtracker.service.CandidateService;
import com.capstone.interviewtracker.service.EmailService;
import com.capstone.interviewtracker.service.UserService;

/**
 * service class for candidate module
 * contains all main logic related to candidate like create, fetch etc
 */
@Service
public class CandidateServiceImpl implements CandidateService {

        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CandidateServiceImpl.class);

        private final CandidateRepository candidateRepository;
        private final JobDescriptionRepository jobDescriptionRepository;
        private final InterviewRepository interviewRepository;
        private final FeedbackRepository feedbackRepository;
        private final UserRepository userRepository;
        private final UserService userService;
        private final EmailService emailService;

        /**
         * Initializes CandidateServiceImpl with required dependencies.
         *
         * @param candidateRepository      repository for candidate data
         * @param jobDescriptionRepository repository for job data
         * @param interviewRepository      repository for interview data
         * @param feedbackRepository       repository for feedback data
         * @param userRepository           repository for user data
         * @param userService              service for user operations
         * @param emailService             service for sending emails
         */
        public CandidateServiceImpl(
                        CandidateRepository candidateRepository,
                        JobDescriptionRepository jobDescriptionRepository,
                        InterviewRepository interviewRepository,
                        FeedbackRepository feedbackRepository,
                        UserRepository userRepository,
                        UserService userService,
                        EmailService emailService) {

                this.candidateRepository = candidateRepository;
                this.jobDescriptionRepository = jobDescriptionRepository;
                this.interviewRepository = interviewRepository;
                this.feedbackRepository = feedbackRepository;
                this.userRepository = userRepository;
                this.userService = userService;
                this.emailService = emailService;
        }

        /**
         * Returns application status for a candidate using email.
         *
         * @param email candidate email
         * @return application status details, or null if candidate not found
         */
        @Override
        public ApplicationStatusDTO getApplicationStatusByEmail(String email) {

                logger.info("Fetching application status for email: {}", email);

                logger.debug("Looking up candidate in database for email: {}", email);
                Candidate candidate = candidateRepository.findByEmail(email).orElse(null);

                if (candidate == null) {
                        logger.warn("No candidate found for email: {} - returning null", email);
                        return null;
                }

                logger.debug("Candidate found for email: {} - ID: {}, status: {}, stage: {}",
                                email, candidate.getId(), candidate.getStatus(), candidate.getCurrentStage());

                logger.debug("Fetching interviews for candidateId: {}", candidate.getId());
                List<Interview> interviews = interviewRepository.findByCandidateIdWithPanels(
                                candidate.getId());

                logger.debug("Found {} interview(s) for candidateId: {}", interviews.size(), candidate.getId());

                List<InterviewSummary> summaries = interviews.stream()
                                .map(iv -> {
                                        logger.debug("Building interview summary for interviewId: {}, stage: {}",
                                                        iv.getId(), iv.getStage());

                                        InterviewSummary s = new InterviewSummary();
                                        s.setInterviewId(iv.getId());
                                        s.setStage(iv.getStage());
                                        s.setScheduledAt(iv.getScheduledAt());
                                        s.setInterviewStatus(iv.getStatus());

                                        List<String> names = iv.getPanels().stream()
                                                        .map(Panel::getName)
                                                        .collect(Collectors.toList());

                                        s.setPanelNames(names);

                                        boolean fbDone = !feedbackRepository
                                                        .findByInterviewId(iv.getId())
                                                        .isEmpty();

                                        logger.debug("Feedback submitted for interviewId: {} - {}", iv.getId(), fbDone);

                                        s.setFeedbackSubmitted(fbDone);

                                        return s;
                                })
                                .collect(Collectors.toList());

                logger.debug("Building ApplicationStatusDTO for candidateId: {}", candidate.getId());

                ApplicationStatusDTO dto = new ApplicationStatusDTO();
                dto.setCandidateId(candidate.getId());
                dto.setCandidateName(candidate.getName());
                dto.setCandidateEmail(candidate.getEmail());
                dto.setCurrentStage(candidate.getCurrentStage());
                dto.setApplicationStatus(candidate.getStatus());

                dto.setLocked(candidate.getStatus() != CandidateStatus.REJECTED);

                dto.setResumeUploaded(candidate.getResumeUrl() != null
                                && !candidate.getResumeUrl().isBlank());

                JobDescription job = candidate.getJobDescription();
                dto.setJobId(job.getId());
                dto.setJobTitle(job.getTitle());

                dto.setInterviews(summaries);

                dto.setDerivedStatus(buildDerivedStatus(
                                candidate.getStatus(),
                                candidate.getCurrentStage()));

                logger.info("Application status fetched successfully for email: {}, derivedStatus: {}",
                                email, dto.getDerivedStatus());

                return dto;
        }

        /**
         * Builds a user friendly status label for the progress card.
         *
         * @param status candidate status
         * @param stage  current interview stage
         * @return derived display status
         */
        private String buildDerivedStatus(
                        CandidateStatus status,
                        Stage stage) {

                logger.debug("Building derived status for candidateStatus: {}, stage: {}", status, stage);

                if (status == CandidateStatus.REJECTED) {
                        return "Rejected";
                }

                if (status == CandidateStatus.SELECTED) {
                        return "Completed";
                }

                if (stage == null) {
                        return "Applied";
                }

                switch (stage) {
                        case PROFILING:
                                return "Applied";

                        case SCREENING:
                                return "Screening";

                        case L1:
                                return "L1";

                        case L2:
                                return "L2";

                        case HR:
                                return "HR";

                        default:
                                return "Applied";
                }
        }

        /**
         * Creates a new candidate or updates an existing rejected application.
         *
         * @param request candidate request data
         * @return created or updated candidate response
         */
        @Override
        public CandidateResponseDTO createCandidate(
                        CandidateRequestDTO request) {

                logger.info("Create candidate request received for email: {}, jobId: {}",
                                request.getEmail(), request.getJobId());

                logger.debug("Checking for active application by email: {}", request.getEmail());
                List<Candidate> activeByEmail = candidateRepository.findActiveByEmail(
                                request.getEmail(),
                                CandidateStatus.REJECTED);

                if (!activeByEmail.isEmpty()) {
                        logger.warn("Create candidate failed - active application exists for email: {}",
                                        request.getEmail());
                        throw new ConflictException(
                                        CandidateMessages.ACTIVE_APPLICATION_EXISTS);
                }

                logger.debug("Checking for active application by phone: {}", request.getPhone());
                List<Candidate> activeByPhone = candidateRepository.findActiveByPhone(
                                request.getPhone(),
                                CandidateStatus.REJECTED);

                if (!activeByPhone.isEmpty()) {
                        logger.warn("Create candidate failed - active application exists for phone: {}",
                                        request.getPhone());
                        throw new ConflictException(
                                        CandidateMessages.ACTIVE_APPLICATION_PHONE_EXISTS);
                }

                logger.debug("Fetching job description from database for jobId: {}", request.getJobId());
                JobDescription job = jobDescriptionRepository.findById(request.getJobId())
                                .orElseThrow(() -> {
                                        logger.warn("Create candidate failed - job not found for ID: {}",
                                                        request.getJobId());
                                        return new ResourceNotFoundException(
                                                        JobMessages.JOB_NOT_FOUND + ": " + request.getJobId());
                                });

                if (!job.isActive()) {
                        logger.warn("Create candidate failed - job is not active for ID: {}", request.getJobId());
                        throw new BadRequestException(
                                        JobMessages.JOB_NOT_ACTIVE);
                }

                Integer applicantAge = null;

                /* Resolve DOB: prefer request value; fall back to linked user account */
                LocalDate applicantDob = request.getDateOfBirth();

                logger.debug("Looking up user account for candidate email: {}", request.getEmail());
                User linkedUser = userRepository.findByEmail(
                                request.getEmail().trim().toLowerCase())
                                .orElse(null);

                if (applicantDob == null && linkedUser != null) {
                        logger.debug("DOB not in request - using DOB from linked user account for email: {}",
                                        request.getEmail());
                        applicantDob = linkedUser.getDateOfBirth();
                }

                if (applicantDob == null) {
                        logger.warn("Create candidate failed - date of birth is required but not provided for email: {}",
                                        request.getEmail());
                        throw new BadRequestException(
                                        ValidationMessages.DOB_REQUIRED);
                }

                applicantAge = Period.between(applicantDob, LocalDate.now()).getYears();

                if (applicantAge < 18) {
                        logger.warn("Create candidate failed - age {} is below minimum (18) for email: {}",
                                        applicantAge,
                                        request.getEmail());
                        throw new BadRequestException(
                                        ValidationMessages.DOB_UNDERAGE);
                }

                if (applicantAge > 60) {
                        logger.warn("Create candidate failed - age {} exceeds maximum (60) for email: {}", applicantAge,
                                        request.getEmail());
                        throw new BadRequestException(
                                        ValidationMessages.DOB_OVER_MAX_AGE);
                }

                logger.debug("Checking for existing candidate record for email: {}", request.getEmail());
                Candidate existing = null;

                if (linkedUser != null) {
                        existing = candidateRepository
                                        .findByUserId(linkedUser.getId())
                                        .orElse(null);
                }

                if (existing == null) {
                        existing = candidateRepository
                                        .findByEmail(request.getEmail().trim().toLowerCase())
                                        .orElse(null);
                }

                Candidate candidate;

                if (existing != null) {

                        if (existing.getStatus() != CandidateStatus.REJECTED) {
                                logger.warn("Create candidate failed - existing non-rejected application found for email: {}",
                                                request.getEmail());
                                throw new ConflictException(
                                                CandidateMessages.ACTIVE_APPLICATION_EXISTS);
                        }

                        logger.info("Re-using existing rejected candidate record for email: {}, candidateId: {}",
                                        request.getEmail(), existing.getId());
                        candidate = existing;
                        candidate.setApplicationId(
                                        nextApplicationId(candidate));

                        logger.debug("New applicationId assigned: {} for candidateId: {}",
                                        candidate.getApplicationId(), candidate.getId());

                } else {
                        logger.info("No existing record found - creating new candidate for email: {}",
                                        request.getEmail());
                        candidate = new Candidate();
                }

                candidate.setName(request.getName().trim());
                candidate.setEmail(request.getEmail().trim().toLowerCase());
                candidate.setPhone(request.getPhone().trim());
                candidate.setDateOfBirth(applicantDob);
                candidate.setCurrentOrganization(
                                request.getCurrentOrganization());
                candidate.setTotalExperience(request.getTotalExperience());
                candidate.setRelevantExperience(request.getRelevantExperience());
                candidate.setCurrentCTC(request.getCurrentCTC());
                candidate.setExpectedCTC(request.getExpectedCTC());
                candidate.setNoticePeriod(request.getNoticePeriod());
                candidate.setPreferredLocation(request.getPreferredLocation());
                candidate.setSource(request.getSource());
                candidate.setJobDescription(job);
                candidate.setCurrentStage(Stage.PROFILING);
                candidate.setStatus(CandidateStatus.IN_PROGRESS);

                if (existing == null) {
                        candidate.setApplicationId(1);
                }

                if (linkedUser != null) {
                        candidate.setUser(linkedUser);

                        if (linkedUser.getDateOfBirth() == null) {
                                logger.debug("Updating dateOfBirth on linked user account for email: {}",
                                                request.getEmail());
                                linkedUser.setDateOfBirth(applicantDob);
                                userRepository.save(linkedUser);
                        }

                } else if (existing == null) {
                        userRepository.findByEmail(candidate.getEmail())
                                        .ifPresent(candidate::setUser);
                }

                logger.debug("Saving candidate to database for email: {}", candidate.getEmail());
                Candidate saved = candidateRepository.save(candidate);
                logger.info("Candidate saved successfully with ID: {} for email: {}", saved.getId(), saved.getEmail());

                logger.debug("Triggering onboarding email check for candidateId: {}", saved.getId());
                triggerOnboardingEmailIfNeeded(saved, applicantDob);

                return mapToResponse(saved);
        }

        /**
         * Re-applies a candidate to a new job after rejection.
         *
         * @param candidateId candidate id
         * @param newJobId    new job id
         * @param email       mail id
         * @return updated candidate response
         */
        @Override
        public CandidateResponseDTO reApply(
                        Long candidateId,
                        Long newJobId,
                        String email) {

                logger.info("Re-apply request received for candidateId: {}, newJobId: {}", candidateId, newJobId);

                Candidate candidate = candidateRepository.findById(candidateId)
                                .orElseThrow(() -> {
                                        logger.warn("Re-apply failed - candidate not found for ID: {}", candidateId);
                                        return new ResourceNotFoundException(
                                                        CandidateMessages.CANDIDATE_NOT_FOUND + ": " + candidateId);
                                });

                if (!candidate.getEmail().equalsIgnoreCase(email)) {
                        logger.warn("Unauthorized re-apply attempt by user: {} for candidateId: {}", email,
                                        candidateId);
                        throw new UnauthorizedException("You cannot reapply for another candidate");
                }

                if (candidate.getStatus() != CandidateStatus.REJECTED) {
                        logger.warn("Re-apply failed - candidate not REJECTED. Current status: {} for candidateId: {}",
                                        candidate.getStatus(), candidateId);

                        throw new ConflictException(
                                        CandidateMessages.REAPPLICATION_ALLOWED_ONLY_AFTER_REJECTION);
                }

                JobDescription newJob = jobDescriptionRepository.findById(newJobId)
                                .orElseThrow(() -> {
                                        logger.warn("Re-apply failed - job not found for ID: {}", newJobId);
                                        return new ResourceNotFoundException(
                                                        JobMessages.JOB_NOT_FOUND + ": " + newJobId);
                                });

                if (!newJob.isActive()) {
                        logger.warn("Re-apply failed - job not active for ID: {}", newJobId);
                        throw new BadRequestException(JobMessages.JOB_NOT_ACTIVE);
                }

                candidate.setJobDescription(newJob);
                candidate.setCurrentStage(Stage.PROFILING);
                candidate.setStatus(CandidateStatus.IN_PROGRESS);
                candidate.setApplicationId(nextApplicationId(candidate));

                Candidate saved = candidateRepository.save(candidate);

                logger.info("Candidate re-applied successfully - candidateId: {}, newJobId: {}, applicationId: {}",
                                saved.getId(), newJobId, saved.getApplicationId());

                return mapToResponse(saved);
        }

        /**
         * Returns next application id for a candidate.
         *
         * @param candidate candidate entity
         * @return incremented application id
         */
        private Integer nextApplicationId(Candidate candidate) {

                Integer current = candidate.getApplicationId();

                logger.debug("Calculating next applicationId - current: {} for candidateId: {}",
                                current, candidate.getId());

                return (current == null) ? 1 : current + 1;
        }

        /**
         * Sends onboarding email if candidate does not have a valid login.
         * Ensures a User account exists and triggers password setup email.
         *
         * @param candidate candidate entity
         * @param dob       candidate date of birth
         */
        private void triggerOnboardingEmailIfNeeded(
                        Candidate candidate,
                        LocalDate dob) {

                String email = candidate.getEmail();

                logger.debug("Checking if onboarding email is needed for candidateId: {}", candidate.getId());

                if (email == null || email.isBlank()) {
                        logger.warn("Onboarding email skipped - email is null or blank for candidateId: {}",
                                        candidate.getId());
                        return;
                }

                User linked = candidate.getUser();

                if (linked == null) {
                        logger.debug("No linked user on candidate - looking up by email: {}", email);
                        linked = userRepository.findByEmail(email).orElse(null);
                }

                /* If user already has password, assume login exists (no action needed) */
                if (linked != null
                                && linked.getPassword() != null
                                && !linked.getPassword().isEmpty()) {
                        logger.debug("User already has a password for email: {} - skipping onboarding email", email);
                        return;
                }

                if (linked == null) {
                        logger.info("No user account found for email: {} - creating new user for onboarding", email);
                        linked = new User();
                        linked.setName(candidate.getName());
                        linked.setEmail(email);
                        linked.setMobile(candidate.getPhone());
                        linked.setDateOfBirth(dob);
                        linked.setRole(Role.CANDIDATE);
                        linked.setPassword(null);
                        linked.setEnabled(false);

                        logger.debug("Saving new user account for onboarding email: {}", email);
                        linked = userRepository.save(linked);
                        logger.info("New user account created with ID: {} for onboarding email: {}", linked.getId(),
                                        email);

                        candidate.setUser(linked);
                        candidateRepository.save(candidate);
                        logger.debug("Candidate updated with linked userId: {} for candidateId: {}",
                                        linked.getId(), candidate.getId());
                }

                try {
                        logger.debug("Generating password setup link for onboarding email: {}", email);
                        String setLink = userService.createTokenAndBuildLink(
                                        linked.getEmail(),
                                        linked.getRole().name());

                        logger.info("Sending onboarding email to candidate: {}", email);
                        emailService.sendCandidateOnboardingEmail(
                                        linked.getEmail(),
                                        linked.getName(),
                                        setLink);
                        logger.info("Onboarding email sent successfully to: {}", email);

                } catch (Exception e) {
                        logger.error(
                                        "Failed to send onboarding email for {}",
                                        email,
                                        e);
                }
        }

        /**
         * Updates resume path for a candidate.
         *
         * @param candidateId   candidate id
         * @param resumePath    stored resume file path
         * @param loggedInEmail email of the authenticated user for authorization check
         * @return updated candidate response
         */
        @Override
        public CandidateResponseDTO updateResumePath(
                        Long candidateId,
                        String resumePath,
                        String loggedInEmail) {

                logger.info("Update resume path request received for candidateId: {}", candidateId);

                Candidate candidate = candidateRepository.findById(candidateId)
                                .orElseThrow(() -> {
                                        logger.warn("Candidate not found for ID: {}", candidateId);
                                        return new ResourceNotFoundException(
                                                        CandidateMessages.CANDIDATE_NOT_FOUND + ": " + candidateId);
                                });

                User user = userRepository.findByEmail(loggedInEmail)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                /* SECURITY CHECK */
                if (user.getRole().name().equals("CANDIDATE")) {

                        if (!candidate.getEmail().equalsIgnoreCase(loggedInEmail)) {
                                logger.warn("Unauthorized resume update attempt by user: {}", loggedInEmail);
                                throw new UnauthorizedException(
                                                "You are not allowed to update another candidate's resume");
                        }
                }

                /* HR is allowed to update any candidate */

                logger.debug("Updating resume path for candidateId: {} to: {}", candidateId, resumePath);

                candidate.setResumeUrl(resumePath);

                Candidate saved = candidateRepository.save(candidate);

                logger.info("Resume updated successfully for candidateId: {}", candidateId);

                return mapToResponse(saved);
        }

        /**
         * Returns all candidates from the database.
         *
         * @return list of candidate responses
         */
        @Override
        public List<CandidateResponseDTO> getAllCandidates() {

                logger.info("Fetching all candidates from the database");

                logger.debug("Calling candidateRepository.findAll()");

                List<CandidateResponseDTO> candidates = candidateRepository.findAll()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();

                logger.info("Successfully fetched {} candidate(s) from the database", candidates.size());

                return candidates;
        }

        /**
         * Returns candidate details by id.
         * Throws exception if candidate is not found.
         *
         * @param id candidate id
         * @return candidate response
         */
        @Override
        public CandidateResponseDTO getCandidateById(Long id) {

                logger.info("Fetching candidate for ID: {}", id);

                logger.debug("Looking up candidate in database for ID: {}", id);
                Candidate candidate = candidateRepository.findById(id)
                                .orElseThrow(() -> {
                                        logger.warn("Get candidate failed - candidate not found for ID: {}", id);
                                        return new ResourceNotFoundException(
                                                        CandidateMessages.CANDIDATE_NOT_FOUND + ": " + id);
                                });

                logger.debug("Candidate found for ID: {}, email: {}", id, candidate.getEmail());

                return mapToResponse(candidate);
        }

        /**
         * Advances candidate to next interview stage.
         * Ensures all required conditions are met before progression.
         *
         * @param candidateId candidate id
         * @return updated candidate response
         */
        @Override
        public CandidateResponseDTO advanceStage(Long candidateId) {

                logger.info("Advance stage request received for candidateId: {}", candidateId);

                logger.debug("Looking up candidate in database for ID: {}", candidateId);
                Candidate candidate = candidateRepository.findById(candidateId)
                                .orElseThrow(() -> {
                                        logger.warn("Advance stage failed - candidate not found for ID: {}",
                                                        candidateId);
                                        return new ResourceNotFoundException(
                                                        CandidateMessages.CANDIDATE_NOT_FOUND + ": " + candidateId);
                                });

                if (candidate.getStatus() == CandidateStatus.REJECTED) {
                        logger.warn("Advance stage failed - candidate is REJECTED for ID: {}", candidateId);
                        throw new BadRequestException(
                                        CandidateMessages.CANNOT_ADVANCE_REJECTED_CANDIDATE);
                }

                if (candidate.getStatus() == CandidateStatus.SELECTED) {
                        logger.warn("Advance stage failed - candidate is already SELECTED for ID: {}", candidateId);
                        throw new ConflictException(
                                        CandidateMessages.CANDIDATE_ALREADY_SELECTED);
                }

                Stage currentStage = candidate.getCurrentStage();

                logger.debug("Current stage for candidateId: {} is: {}", candidateId, currentStage);

                if (currentStage == Stage.L1
                                || currentStage == Stage.L2
                                || currentStage == Stage.HR) {

                        logger.debug("Fetching interview for feedback check at stage: {} for candidateId: {}",
                                        currentStage, candidateId);

                        Optional<Interview> located;

                        if (candidate.getApplicationId() == null) {
                                located = interviewRepository
                                                .findByCandidateIdAndStage(
                                                                candidateId,
                                                                currentStage);
                        } else {
                                located = interviewRepository
                                                .findByCandidateIdAndApplicationIdAndStage(
                                                                candidateId,
                                                                candidate.getApplicationId(),
                                                                currentStage);
                        }

                        Interview interview = located.orElseThrow(() -> {
                                logger.warn("Advance stage failed - no interview found at stage: {} for candidateId: {}",
                                                currentStage, candidateId);
                                return new BadRequestException(
                                                InterviewMessages.FEEDBACK_ONLY_AFTER_INTERVIEW_COMPLETION);
                        });

                        int assignedPanels = interview.getPanels() == null
                                        ? 0
                                        : interview.getPanels().size();

                        int submittedFeedback = feedbackRepository
                                        .findByInterviewId(interview.getId())
                                        .size();

                        logger.debug("Feedback check - interviewId: {}, assignedPanels: {}, submittedFeedback: {}",
                                        interview.getId(), assignedPanels, submittedFeedback);

                        if (assignedPanels == 0
                                        || submittedFeedback < assignedPanels) {
                                logger.warn("Advance stage failed - feedback incomplete at stage: {} for candidateId: {}. "
                                                +
                                                "Assigned: {}, Submitted: {}", currentStage, candidateId,
                                                assignedPanels, submittedFeedback);
                                throw new BadRequestException(
                                                InterviewMessages.ALL_PANEL_FEEDBACK_REQUIRED_BEFORE_STAGE_CHANGE);
                        }
                }

                Stage nextStage;

                switch (currentStage) {

                        case PROFILING:
                                nextStage = Stage.SCREENING;
                                break;

                        case SCREENING:
                                nextStage = Stage.L1;
                                break;

                        case L1:
                                nextStage = Stage.L2;
                                break;

                        case L2:
                                nextStage = Stage.HR;
                                break;

                        case HR:
                                logger.info("Candidate at HR stage - marking as SELECTED for candidateId: {}",
                                                candidateId);
                                candidate.setStatus(CandidateStatus.SELECTED);
                                candidateRepository.save(candidate);
                                logger.info("Candidate marked as SELECTED successfully for candidateId: {}",
                                                candidateId);
                                return mapToResponse(candidate);

                        default:
                                logger.warn("Advance stage failed - invalid stage transition from: {} for candidateId: {}",
                                                currentStage, candidateId);
                                throw new BadRequestException(
                                                InterviewMessages.INVALID_STAGE_TRANSITION + ": " + currentStage);
                }

                logger.info("Advancing candidateId: {} from stage: {} to stage: {}", candidateId, currentStage,
                                nextStage);
                candidate.setCurrentStage(nextStage);

                logger.debug("Saving updated stage for candidateId: {}", candidateId);
                candidateRepository.save(candidate);
                logger.info("Stage advanced successfully for candidateId: {} - new stage: {}", candidateId, nextStage);

                return mapToResponse(candidate);
        }

        /**
         * Rejects a candidate application.
         *
         * @param candidateId candidate id
         * @return updated candidate response
         */
        @Override
        public CandidateResponseDTO rejectCandidate(Long candidateId) {

                logger.info("Reject candidate request received for candidateId: {}", candidateId);

                logger.debug("Looking up candidate in database for ID: {}", candidateId);
                Candidate candidate = candidateRepository.findById(candidateId)
                                .orElseThrow(() -> {
                                        logger.warn("Reject candidate failed - candidate not found for ID: {}",
                                                        candidateId);
                                        return new ResourceNotFoundException(
                                                        CandidateMessages.CANDIDATE_NOT_FOUND + ": "
                                                                        + candidateId);
                                });

                if (candidate.getStatus() == CandidateStatus.REJECTED) {
                        logger.warn("Reject candidate failed - candidate is already REJECTED for ID: {}", candidateId);
                        throw new ConflictException(
                                        CandidateMessages.CANDIDATE_ALREADY_REJECTED);
                }

                logger.debug("Setting candidate status to REJECTED for candidateId: {}", candidateId);
                candidate.setStatus(CandidateStatus.REJECTED);

                logger.debug("Saving rejected candidate to database for candidateId: {}", candidateId);
                candidateRepository.save(candidate);
                logger.info("Candidate rejected successfully for candidateId: {}", candidateId);

                return mapToResponse(candidate);
        }

        /**
         * Converts Candidate entity to response DTO.
         *
         * @param candidate candidate entity
         * @return mapped response DTO
         */
        private CandidateResponseDTO mapToResponse(Candidate candidate) {

                logger.debug("Mapping candidate entity to response DTO for candidateId: {}", candidate.getId());

                CandidateResponseDTO response = new CandidateResponseDTO();

                response.setId(candidate.getId());
                response.setName(candidate.getName());
                response.setEmail(candidate.getEmail());
                response.setPhone(candidate.getPhone());
                response.setDateOfBirth(candidate.getDateOfBirth());
                response.setResumeUrl(candidate.getResumeUrl());
                response.setCurrentOrganization(candidate.getCurrentOrganization());
                response.setTotalExperience(candidate.getTotalExperience());
                response.setRelevantExperience(candidate.getRelevantExperience());
                response.setCurrentCTC(candidate.getCurrentCTC());
                response.setExpectedCTC(candidate.getExpectedCTC());
                response.setNoticePeriod(candidate.getNoticePeriod());
                response.setPreferredLocation(candidate.getPreferredLocation());
                response.setSource(candidate.getSource());
                response.setCurrentStage(candidate.getCurrentStage());
                response.setStatus(candidate.getStatus());

                response.setJobId(candidate.getJobDescription().getId());
                response.setJobTitle(candidate.getJobDescription().getTitle());

                return response;
        }

}