package com.capstone.interviewtracker.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.capstone.interviewtracker.exception.custom.BadRequestException;
import com.capstone.interviewtracker.exception.custom.ConflictException;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
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

        Candidate candidate = candidateRepository.findByEmail(email).orElse(null);

        if (candidate == null) {
            return null;
        }

        List<Interview> interviews = interviewRepository.findByCandidateIdWithPanels(
                candidate.getId());

        List<InterviewSummary> summaries = interviews.stream()
                .map(iv -> {
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

                    s.setFeedbackSubmitted(fbDone);

                    return s;
                })
                .collect(Collectors.toList());

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

        return dto;
    }

    /**
     * Builds a user-friendly status label for the progress card.
     *
     * @param status candidate status
     * @param stage  current interview stage
     * @return derived display status
     */
    private String buildDerivedStatus(
            CandidateStatus status,
            Stage stage) {

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

        List<Candidate> activeByEmail = candidateRepository.findActiveByEmail(
                request.getEmail(),
                CandidateStatus.REJECTED);

        if (!activeByEmail.isEmpty()) {
            throw new ConflictException(
                    CandidateMessages.ACTIVE_APPLICATION_EXISTS);
        }

        List<Candidate> activeByPhone = candidateRepository.findActiveByPhone(
                request.getPhone(),
                CandidateStatus.REJECTED);

        if (!activeByPhone.isEmpty()) {
            throw new ConflictException(
                    CandidateMessages.ACTIVE_APPLICATION_PHONE_EXISTS);
        }

        JobDescription job = jobDescriptionRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        JobMessages.JOB_NOT_FOUND + ": " + request.getJobId()));

        if (!job.isActive()) {
            throw new BadRequestException(
                    JobMessages.JOB_NOT_ACTIVE);
        }

        Integer applicantAge = request.getAge();

        User linkedUser = userRepository.findByEmail(
                request.getEmail().trim().toLowerCase())
                .orElse(null);

        if (applicantAge == null && linkedUser != null) {
            applicantAge = linkedUser.getAge();
        }

        if (applicantAge == null) {
            throw new BadRequestException(
                    ValidationMessages.AGE_REQUIRED);
        }

        if (applicantAge < 18) {
            throw new BadRequestException(
                    ValidationMessages.AGE_MIN);
        }

        if (applicantAge > 60) {
            throw new BadRequestException(
                    ValidationMessages.AGE_MAX);
        }

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
                throw new ConflictException(
                        CandidateMessages.ACTIVE_APPLICATION_EXISTS);
            }

            candidate = existing;
            candidate.setApplicationId(
                    nextApplicationId(candidate));

        } else {
            candidate = new Candidate();
        }

        candidate.setName(request.getName().trim());
        candidate.setEmail(request.getEmail().trim().toLowerCase());
        candidate.setPhone(request.getPhone().trim());
        candidate.setAge(applicantAge);
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

            if (linkedUser.getAge() == null) {
                linkedUser.setAge(applicantAge);
                userRepository.save(linkedUser);
            }

        } else if (existing == null) {
            userRepository.findByEmail(candidate.getEmail())
                    .ifPresent(candidate::setUser);
        }

        Candidate saved = candidateRepository.save(candidate);

        triggerOnboardingEmailIfNeeded(saved, applicantAge);

        return mapToResponse(saved);
    }

    /**
     * Re-applies a candidate to a new job after rejection.
     *
     * @param candidateId candidate id
     * @param newJobId    new job id
     * @return updated candidate response
     */
    @Override
    public CandidateResponseDTO reApply(
            Long candidateId,
            Long newJobId) {

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CandidateMessages.CANDIDATE_NOT_FOUND + ": " + candidateId));

        if (candidate.getStatus() != CandidateStatus.REJECTED) {
            throw new ConflictException(
                    CandidateMessages.REAPPLICATION_ALLOWED_ONLY_AFTER_REJECTION);
        }

        JobDescription newJob = jobDescriptionRepository.findById(newJobId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        JobMessages.JOB_NOT_FOUND + ": " + newJobId));

        if (!newJob.isActive()) {
            throw new BadRequestException(
                    JobMessages.JOB_NOT_ACTIVE);
        }

        candidate.setJobDescription(newJob);
        candidate.setCurrentStage(Stage.PROFILING);
        candidate.setStatus(CandidateStatus.IN_PROGRESS);

        candidate.setApplicationId(
                nextApplicationId(candidate));

        Candidate saved = candidateRepository.save(candidate);

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

        return (current == null) ? 1 : current + 1;
    }

    /**
     * Sends onboarding email if candidate does not have a valid login.
     * Ensures a User account exists and triggers password setup email.
     *
     * @param candidate candidate entity
     * @param age       candidate age
     */
    private void triggerOnboardingEmailIfNeeded(
            Candidate candidate,
            Integer age) {

        String email = candidate.getEmail();

        if (email == null || email.isBlank()) {
            return;
        }

        User linked = candidate.getUser();

        if (linked == null) {
            linked = userRepository.findByEmail(email).orElse(null);
        }

        // If user already has password, assume login exists (no action needed)
        if (linked != null
                && linked.getPassword() != null
                && !linked.getPassword().isEmpty()) {
            return;
        }

        if (linked == null) {
            linked = new User();
            linked.setName(candidate.getName());
            linked.setEmail(email);
            linked.setMobile(candidate.getPhone());
            linked.setAge(age);
            linked.setRole(Role.CANDIDATE);
            linked.setPassword(null);
            linked.setEnabled(false);

            linked = userRepository.save(linked);

            candidate.setUser(linked);
            candidateRepository.save(candidate);
        }

        try {
            String setLink = userService.createTokenAndBuildLink(
                    linked.getEmail(),
                    linked.getRole().name());

            emailService.sendCandidateOnboardingEmail(
                    linked.getEmail(),
                    linked.getName(),
                    setLink);

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
     * @param candidateId candidate id
     * @param resumePath  stored resume file path
     * @return updated candidate response
     */
    @Override
    public CandidateResponseDTO updateResumePath(
            Long candidateId,
            String resumePath) {

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CandidateMessages.CANDIDATE_NOT_FOUND + ": " + candidateId));

        candidate.setResumeUrl(resumePath);

        return mapToResponse(
                candidateRepository.save(candidate));
    }

    /**
     * Returns all candidates from the database.
     *
     * @return list of candidate responses
     */
    @Override
    public List<CandidateResponseDTO> getAllCandidates() {

        return candidateRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
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

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CandidateMessages.CANDIDATE_NOT_FOUND + ": " + id));

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

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CandidateMessages.CANDIDATE_NOT_FOUND + ": " + candidateId));

        if (candidate.getStatus() == CandidateStatus.REJECTED) {
            throw new BadRequestException(
                    CandidateMessages.CANNOT_ADVANCE_REJECTED_CANDIDATE);
        }

        if (candidate.getStatus() == CandidateStatus.SELECTED) {
            throw new ConflictException(
                    CandidateMessages.CANDIDATE_ALREADY_SELECTED);
        }

        Stage currentStage = candidate.getCurrentStage();

        if (currentStage == Stage.L1
                || currentStage == Stage.L2
                || currentStage == Stage.HR) {

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

            Interview interview = located.orElseThrow(() -> new BadRequestException(
                    InterviewMessages.FEEDBACK_ONLY_AFTER_INTERVIEW_COMPLETION));

            int assignedPanels = interview.getPanels() == null
                    ? 0
                    : interview.getPanels().size();

            int submittedFeedback = feedbackRepository
                    .findByInterviewId(interview.getId())
                    .size();

            if (assignedPanels == 0
                    || submittedFeedback < assignedPanels) {
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
                candidate.setStatus(CandidateStatus.SELECTED);
                candidateRepository.save(candidate);
                return mapToResponse(candidate);

            default:
                throw new BadRequestException(
                        InterviewMessages.INVALID_STAGE_TRANSITION + ": " + currentStage);
        }

        candidate.setCurrentStage(nextStage);

        candidateRepository.save(candidate);

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

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CandidateMessages.CANDIDATE_NOT_FOUND + ": "
                                + candidateId));

        if (candidate.getStatus() == CandidateStatus.REJECTED) {
            throw new ConflictException(
                    CandidateMessages.CANDIDATE_ALREADY_REJECTED);
        }

        candidate.setStatus(CandidateStatus.REJECTED);

        candidateRepository.save(candidate);

        return mapToResponse(candidate);
    }

    /**
     * Converts Candidate entity to response DTO.
     *
     * @param candidate candidate entity
     * @return mapped response DTO
     */
    private CandidateResponseDTO mapToResponse(Candidate candidate) {

        CandidateResponseDTO response = new CandidateResponseDTO();

        response.setId(candidate.getId());
        response.setName(candidate.getName());
        response.setEmail(candidate.getEmail());
        response.setPhone(candidate.getPhone());
        response.setAge(candidate.getAge());
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