package com.capstone.interviewtracker.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.capstone.interviewtracker.constants.messages.CandidateMessages;
import com.capstone.interviewtracker.constants.messages.InterviewMessages;
import com.capstone.interviewtracker.constants.messages.PanelMessages;
import com.capstone.interviewtracker.dto.Request.InterviewRequestDTO;
import com.capstone.interviewtracker.dto.Response.InterviewResponseDTO;
import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.enums.InterviewStatus;
import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.enums.Stage;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.exception.custom.UnauthorizedException;
import com.capstone.interviewtracker.exception.custom.BadRequestException;
import com.capstone.interviewtracker.exception.custom.ConflictException;
import com.capstone.interviewtracker.model.Candidate;
import com.capstone.interviewtracker.model.Interview;
import com.capstone.interviewtracker.model.Panel;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.CandidateRepository;
import com.capstone.interviewtracker.repository.InterviewRepository;
import com.capstone.interviewtracker.repository.PanelRepository;
import com.capstone.interviewtracker.repository.UserRepository;
import com.capstone.interviewtracker.service.EmailService;
import com.capstone.interviewtracker.service.InterviewService;

@Service
public class InterviewServiceImpl implements InterviewService {

        private static final Logger logger = LoggerFactory.getLogger(InterviewServiceImpl.class);

        private final InterviewRepository interviewRepository;
        private final CandidateRepository candidateRepository;
        private final PanelRepository panelRepository;
        private final UserRepository userRepository;
        private final EmailService emailService;

        /**
         * Constructor injection for InterviewService dependencies.
         *
         * @param interviewRepository interview repository
         * @param candidateRepository candidate repository
         * @param panelRepository     panel repository
         * @param userRepository      user repository
         * @param emailService        email service
         */
        public InterviewServiceImpl(
                        InterviewRepository interviewRepository,
                        CandidateRepository candidateRepository,
                        PanelRepository panelRepository,
                        UserRepository userRepository,
                        EmailService emailService) {

                this.interviewRepository = interviewRepository;
                this.candidateRepository = candidateRepository;
                this.panelRepository = panelRepository;
                this.userRepository = userRepository;
                this.emailService = emailService;
        }

        /**
         * Schedules an interview for a candidate.
         * Enforces stage validation, duplicate checks, and panel assignment rules.
         *
         * @param request interview request data
         * @return scheduled interview response
         */
        @Override
        public InterviewResponseDTO scheduleInterview(InterviewRequestDTO request) {

                logger.info("Schedule interview request received for candidateId: {} at stage: {}",
                                request.getCandidateId(), request.getStage());

                logger.debug("Looking up candidate in database for ID: {}", request.getCandidateId());
                Candidate candidate = candidateRepository.findById(request.getCandidateId())
                                .orElseThrow(() -> {
                                        logger.warn("Schedule interview failed - candidate not found for ID: {}",
                                                        request.getCandidateId());
                                        return new ResourceNotFoundException(
                                                        CandidateMessages.CANDIDATE_NOT_FOUND + " with id: "
                                                                        + request.getCandidateId());
                                });

                logger.debug("Candidate found - ID: {}, status: {}, currentStage: {}",
                                candidate.getId(), candidate.getStatus(), candidate.getCurrentStage());

                if (candidate.getStatus() == CandidateStatus.REJECTED) {
                        logger.warn("Schedule interview failed - candidate is REJECTED for ID: {}", candidate.getId());
                        throw new BadRequestException(
                                        InterviewMessages.CANNOT_SCHEDULE_INTERVIEW_FOR_REJECTED_CANDIDATE);
                }

                if (candidate.getStatus() == CandidateStatus.SELECTED) {
                        logger.warn("Schedule interview failed - candidate is already SELECTED for ID: {}",
                                        candidate.getId());
                        throw new ConflictException(
                                        InterviewMessages.CANNOT_SCHEDULE_INTERVIEW_FOR_SELECTED_CANDIDATE);
                }

                logger.debug("Validating scheduled time: {}", request.getScheduledAt());
                if (request.getScheduledAt() == null
                                || request.getScheduledAt().isBefore(LocalDateTime.now())) {
                        logger.warn("Schedule interview failed - scheduled time is null or in the past: {}",
                                        request.getScheduledAt());
                        throw new BadRequestException(
                                        InterviewMessages.SCHEDULED_TIME_MUST_BE_IN_FUTURE);
                }

                if (candidate.getApplicationId() == null) {
                        logger.debug("ApplicationId is null for candidateId: {} - setting to 1", candidate.getId());
                        candidate.setApplicationId(1);
                        candidate = candidateRepository.save(candidate);
                        logger.debug("Candidate updated with applicationId: 1 for candidateId: {}", candidate.getId());
                }

                Stage requestedStage = request.getStage();
                Stage currentStage = candidate.getCurrentStage();

                logger.debug("Validating stage progression - requestedStage: {}, currentStage: {}", requestedStage,
                                currentStage);

                switch (requestedStage) {

                        case SCREENING:
                                if (currentStage != Stage.PROFILING
                                                && currentStage != Stage.SCREENING) {
                                        logger.warn("Schedule interview failed - cannot schedule SCREENING before PROFILING. "
                                                        +
                                                        "Current stage: {} for candidateId: {}", currentStage,
                                                        candidate.getId());
                                        throw new BadRequestException(
                                                        InterviewMessages.CANNOT_SCHEDULE_SCREENING_BEFORE_PROFILING_COMPLETION
                                                                        + " Current stage: " + currentStage);
                                }
                                break;

                        case L1:
                                if (currentStage == Stage.PROFILING) {
                                        logger.warn("Schedule interview failed - cannot schedule L1 before SCREENING completion. "
                                                        +
                                                        "Current stage: {} for candidateId: {}", currentStage,
                                                        candidate.getId());
                                        throw new BadRequestException(
                                                        InterviewMessages.CANNOT_SCHEDULE_L1_BEFORE_SCREENING_COMPLETION
                                                                        + " Current stage: " + currentStage);
                                }
                                break;

                        case L2:
                                logger.debug("Checking if L1 interview exists for candidateId: {}, applicationId: {}",
                                                candidate.getId(), candidate.getApplicationId());
                                Optional<Interview> l1Interview = interviewRepository
                                                .findByCandidateIdAndApplicationIdAndStage(
                                                                request.getCandidateId(),
                                                                candidate.getApplicationId(),
                                                                Stage.L1);

                                if (l1Interview.isEmpty()) {
                                        logger.warn("Schedule interview failed - L1 interview not found for candidateId: {}",
                                                        candidate.getId());
                                        throw new ResourceNotFoundException(
                                                        InterviewMessages.L1_INTERVIEW_NOT_SCHEDULED);
                                }

                                if (l1Interview.get().getStatus() != InterviewStatus.COMPLETED) {
                                        logger.warn("Schedule interview failed - L1 not completed for candidateId: {}, L1 status: {}",
                                                        candidate.getId(), l1Interview.get().getStatus());
                                        throw new BadRequestException(
                                                        InterviewMessages.L1_MUST_BE_COMPLETED_BEFORE_L2
                                                                        + " Status: " + l1Interview.get().getStatus());
                                }
                                break;

                        case HR:
                                logger.debug("Checking if L2 interview exists for candidateId: {}, applicationId: {}",
                                                candidate.getId(), candidate.getApplicationId());
                                Optional<Interview> l2Interview = interviewRepository
                                                .findByCandidateIdAndApplicationIdAndStage(
                                                                request.getCandidateId(),
                                                                candidate.getApplicationId(),
                                                                Stage.L2);

                                if (l2Interview.isEmpty()) {
                                        logger.warn("Schedule interview failed - L2 interview not found for candidateId: {}",
                                                        candidate.getId());
                                        throw new ResourceNotFoundException(
                                                        InterviewMessages.L2_INTERVIEW_NOT_SCHEDULED);
                                }

                                if (l2Interview.get().getStatus() != InterviewStatus.COMPLETED) {
                                        logger.warn("Schedule interview failed - L2 not completed for candidateId: {}, L2 status: {}",
                                                        candidate.getId(), l2Interview.get().getStatus());
                                        throw new BadRequestException(
                                                        InterviewMessages.L2_MUST_BE_COMPLETED_BEFORE_HR
                                                                        + " Status: " + l2Interview.get().getStatus());
                                }
                                break;

                        default:
                                break;
                }

                logger.debug("Checking for duplicate interview at stage: {} for candidateId: {}", requestedStage,
                                candidate.getId());
                if (interviewRepository.existsByCandidateIdAndApplicationIdAndStage(
                                request.getCandidateId(),
                                candidate.getApplicationId(),
                                requestedStage)) {
                        logger.warn("Schedule interview failed - interview already exists at stage: {} for candidateId: {}",
                                        requestedStage, candidate.getId());
                        throw new ConflictException(
                                        InterviewMessages.INTERVIEW_ALREADY_EXISTS_FOR_STAGE);
                }

                List<Panel> panels;

                if (requestedStage == Stage.HR) {
                        logger.info("Resolving HR panel for HR stage interview - candidateId: {}", candidate.getId());
                        Panel hrPanel = resolveOrCreateHrPanel();
                        panels = List.of(hrPanel);
                        logger.debug("HR panel resolved with ID: {}", hrPanel.getId());

                } else {
                        List<Long> panelIds = request.getPanelIds();

                        logger.debug("Validating panel assignment for stage: {}, panelIds: {}", requestedStage,
                                        panelIds);

                        if (panelIds == null
                                        || panelIds.isEmpty()
                                        || panelIds.size() > 2) {
                                logger.warn("Schedule interview failed - invalid panel count: {} for stage: {}",
                                                panelIds == null ? 0 : panelIds.size(), requestedStage);
                                throw new BadRequestException(
                                                InterviewMessages.PANEL_ASSIGNMENT_MUST_BE_BETWEEN_1_AND_2);
                        }

                        panels = panelIds.stream().map(panelId -> {
                                logger.debug("Fetching panel from database for panelId: {}", panelId);
                                Panel panel = panelRepository.findById(panelId)
                                                .orElseThrow(() -> {
                                                        logger.warn("Schedule interview failed - panel not found for ID: {}",
                                                                        panelId);
                                                        return new ResourceNotFoundException(
                                                                        PanelMessages.NOT_FOUND + panelId);
                                                });

                                if (!panel.isActive()) {
                                        logger.warn("Schedule interview failed - panel is not active for ID: {}",
                                                        panelId);
                                        throw new BadRequestException(
                                                        PanelMessages.PANEL_NOT_ACTIVE + panelId);
                                }

                                return panel;
                        }).toList();
                }

                logger.debug("Building Interview object for candidateId: {}, stage: {}", candidate.getId(),
                                requestedStage);
                Interview interview = new Interview();
                interview.setStage(requestedStage);
                interview.setScheduledAt(request.getScheduledAt());
                interview.setFocusArea(request.getFocusArea());
                interview.setMeetingUrl(request.getMeetingUrl());
                interview.setCandidate(candidate);
                interview.setPanels(panels);
                interview.setStatus(InterviewStatus.SCHEDULED);
                interview.setApplicationId(candidate.getApplicationId());

                logger.debug("Saving interview to database for candidateId: {}, stage: {}", candidate.getId(),
                                requestedStage);
                Interview saved = interviewRepository.save(interview);
                logger.info("Interview scheduled successfully with ID: {} for candidateId: {} at stage: {}",
                                saved.getId(), candidate.getId(), requestedStage);
                logger.info("Sending interview assignment email(s) to {} panel member(s) for interviewId: {}",
                                panels.size(), saved.getId());
                for (Panel panel : panels) {
                        emailService.sendInterviewAssignmentEmail(panel, saved);
                        logger.debug("Assignment email sent to panel member: {} for interviewId: {}", panel.getEmail(),
                                        saved.getId());
                }

                logger.info("Sending interview scheduled email to candidate {} for interviewId: {}",
                                candidate.getEmail(), saved.getId());
                emailService.sendInterviewScheduledEmail(candidate, saved);
                logger.debug("Interview scheduled email sent to candidate: {} for interviewId: {}",
                                candidate.getEmail(), saved.getId());

                return mapToResponse(saved);
        }

        /**
         * Marks an interview as COMPLETED.
         * Ensures only valid interviews can be completed.
         *
         * @param interviewId unique identifier of the interview
         * @return updated interview as InterviewResponseDTO
         */
        @Override
        public InterviewResponseDTO markCompleted(Long interviewId) {

                logger.info("Mark interview as COMPLETED request received for interviewId: {}", interviewId);
                logger.debug("Looking up interview in database for ID: {}", interviewId);
                Interview interview = interviewRepository.findById(interviewId)
                                .orElseThrow(() -> {
                                        logger.warn("Mark completed failed - interview not found for ID: {}",
                                                        interviewId);
                                        return new ResourceNotFoundException(
                                                        InterviewMessages.INTERVIEW_NOT_FOUND + " with id: "
                                                                        + interviewId);
                                });

                logger.debug("Interview found - ID: {}, current status: {}", interviewId, interview.getStatus());

                if (interview.getStatus() == InterviewStatus.COMPLETED) {
                        logger.warn("Mark completed failed - interview is already COMPLETED for ID: {}", interviewId);
                        throw new ConflictException(
                                        InterviewMessages.INTERVIEW_ALREADY_COMPLETED);
                }

                if (interview.getStatus() == InterviewStatus.CANCELLED) {
                        logger.warn("Mark completed failed - interview is CANCELLED for ID: {}", interviewId);
                        throw new BadRequestException(
                                        InterviewMessages.CANNOT_COMPLETE_CANCELLED_INTERVIEW);
                }

                logger.debug("Updating interview status to COMPLETED for interviewId: {}", interviewId);
                interview.setStatus(InterviewStatus.COMPLETED);

                Interview saved = interviewRepository.save(interview);
                logger.info("Interview marked as COMPLETED successfully for interviewId: {}", interviewId);

                return mapToResponse(saved);
        }

        /**
         * Retrieves all interviews from the system.
         * Automatically promotes SCHEDULED interviews to ONGOING if their
         * scheduled time has passed.
         *
         * @return list of all interviews as InterviewResponseDTO
         */
        @Override
        public List<InterviewResponseDTO> getAllInterviews() {

                logger.info("Fetching interviews for logged-in user");

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String email = auth.getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                List<Interview> interviews;

                if (user.getRole() == Role.HR) {

                        logger.debug("HR detected → fetching all interviews");
                        interviews = interviewRepository.findAllWithPanels();

                } else if (user.getRole() == Role.PANEL) {

                        logger.debug("PANEL detected → fetching ONLY assigned interviews");

                        Panel panel = panelRepository.findByUser(user)
                                        .orElseThrow(() -> new ResourceNotFoundException("Panel not found"));

                        interviews = interviewRepository.findInterviewsByPanelId(panel.getId());

                } else {
                        logger.warn("Unauthorized role: {}", user.getRole());
                        throw new BadRequestException("Access denied");
                }

                promoteScheduledToOngoing(interviews);

                return interviews.stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        /**
         * Retrieves all interviews for a given candidate.
         * Also updates interviews from SCHEDULED to ONGOING if their scheduled time
         * has already passed.
         *
         * @param candidateId unique identifier of the candidate
         * @return list of InterviewResponseDTO for the candidate
         */
        @Override
        public List<InterviewResponseDTO> getInterviewsByCandidate(Long candidateId) {

                logger.info("Fetching interviews for candidateId: {}", candidateId);

                logger.debug("Checking if candidate exists for ID: {}", candidateId);
                if (!candidateRepository.existsById(candidateId)) {
                        logger.warn("Get interviews failed - candidate not found for ID: {}", candidateId);
                        throw new ResourceNotFoundException(
                                        CandidateMessages.CANDIDATE_NOT_FOUND + " with id: " + candidateId);
                }

                logger.debug("Fetching interviews from database for candidateId: {}", candidateId);
                List<Interview> interviews = interviewRepository.findByCandidateIdWithPanels(candidateId);

                logger.debug("Checking and promoting SCHEDULED interviews to ONGOING for candidateId: {}", candidateId);
                promoteScheduledToOngoing(interviews);

                List<InterviewResponseDTO> result = interviews.stream()
                                .map(this::mapToResponse)
                                .toList();

                logger.info("Successfully fetched {} interview(s) for candidateId: {}", result.size(), candidateId);

                return result;
        }

        /**
         * Retrieves an interview by its ID.
         * If the interview is SCHEDULED and its scheduled time has passed,
         * it is automatically updated to ONGOING.
         *
         * @param id interview identifier
         * @return interview details as InterviewResponseDTO
         */
        @Override
        public InterviewResponseDTO getInterviewById(Long id) {

                logger.info("Fetching interview for ID: {}", id);

                logger.debug("Looking up interview in database for ID: {}", id);
                Interview interview = interviewRepository.findById(id)
                                .orElseThrow(() -> {
                                        logger.warn("Get interview failed - interview not found for ID: {}", id);
                                        return new ResourceNotFoundException(
                                                        InterviewMessages.INTERVIEW_NOT_FOUND + " with id: " + id);
                                });

                if (interview.getStatus() == InterviewStatus.SCHEDULED
                                && interview.getScheduledAt().isBefore(LocalDateTime.now())) {
                        logger.info("Promoting interview ID: {} from SCHEDULED to ONGOING", id);
                        interview.setStatus(InterviewStatus.ONGOING);
                        interviewRepository.save(interview);
                        logger.debug("Interview ID: {} status updated to ONGOING in database", id);
                }

                return mapToResponse(interview);
        }

        /**
         * Promotes interviews from SCHEDULED to ONGOING if their scheduled time
         * has passed compared to the current system time.
         *
         * @param interviews list of interviews to evaluate and update
         */
        private void promoteScheduledToOngoing(List<Interview> interviews) {

                LocalDateTime now = LocalDateTime.now();

                logger.debug("Running promoteScheduledToOngoing check for {} interview(s)", interviews.size());

                interviews.forEach(interview -> {
                        if (interview.getStatus() == InterviewStatus.SCHEDULED
                                        && interview.getScheduledAt().isBefore(now)) {

                                logger.info("Auto-promoting interview ID: {} from SCHEDULED to ONGOING (scheduled at: {})",
                                                interview.getId(), interview.getScheduledAt());
                                interview.setStatus(InterviewStatus.ONGOING);
                                interviewRepository.save(interview);
                                logger.debug("Interview ID: {} saved with ONGOING status", interview.getId());
                        }
                });
        }

        /**
         * Maps Interview entity to InterviewResponseDTO.
         *
         * @param interview interview entity from database
         * @return mapped InterviewResponseDTO
         */
        private InterviewResponseDTO mapToResponse(Interview interview) {

                logger.debug("Mapping interview entity to response DTO for interviewId: {}", interview.getId());

                InterviewResponseDTO response = new InterviewResponseDTO();
                response.setId(interview.getId());
                response.setStage(interview.getStage());
                response.setScheduledAt(interview.getScheduledAt());
                response.setFocusArea(interview.getFocusArea());
                response.setMeetingUrl(interview.getMeetingUrl());
                response.setCandidateId(interview.getCandidate().getId());
                response.setCandidateName(interview.getCandidate().getName());
                response.setStatus(interview.getStatus());

                List<String> panelNames = interview.getPanels()
                                .stream()
                                .map(Panel::getName)
                                .toList();
                response.setPanelNames(panelNames);

                List<Long> panelIds = interview.getPanels()
                                .stream()
                                .map(Panel::getId)
                                .toList();
                response.setPanelIds(panelIds);

                return response;
        }

        /**
         * Resolves or creates the HR panel used for HR-stage interviews.
         * The system maintains a single HR user, which is mirrored as a Panel
         * so that existing feedback APIs can be reused without schema changes.
         *
         * @return HR Panel instance, never null
         */
        private Panel resolveOrCreateHrPanel() {

                logger.info("Resolving or creating HR panel for hr@company.com");

                logger.debug("Looking up HR user in database for email: hr@company.com");
                User hrUser = userRepository.findByEmail("hr@company.com")
                                .orElseThrow(() -> {
                                        logger.error("HR user not found for email: hr@company.com - cannot schedule HR interview");
                                        return new ResourceNotFoundException(
                                                        InterviewMessages.HR_USER_NOT_FOUND_CANNOT_SCHEDULE_INTERVIEW);
                                });

                if (hrUser.getRole() != Role.HR) {
                        logger.error("Configured HR account hr@company.com does not have HR role - actual role: {}",
                                        hrUser.getRole());
                        throw new UnauthorizedException(
                                        InterviewMessages.CONFIGURED_HR_ACCOUNT_NOT_IN_HR_ROLE);
                }

                logger.debug("Checking if HR panel already exists for email: hr@company.com");
                Optional<Panel> existing = panelRepository.findByEmail(hrUser.getEmail());

                if (existing.isPresent()) {
                        Panel panel = existing.get();

                        logger.debug("Existing HR panel found with ID: {}, checking if update needed", panel.getId());

                        boolean updated = false;

                        if (!panel.isActive()) {
                                logger.info("HR panel ID: {} is inactive - reactivating", panel.getId());
                                panel.setActive(true);
                                updated = true;
                        }

                        if (panel.getUser() == null
                                        || !panel.getUser().getId().equals(hrUser.getId())) {
                                logger.info("HR panel ID: {} user link is missing or outdated - updating to hrUserId: {}",
                                                panel.getId(), hrUser.getId());
                                panel.setUser(hrUser);
                                updated = true;
                        }

                        if (updated) {
                                logger.debug("Saving updated HR panel to database for ID: {}", panel.getId());
                                Panel savedPanel = panelRepository.save(panel);
                                logger.info("HR panel updated and saved with ID: {}", savedPanel.getId());
                                return savedPanel;
                        }

                        logger.debug("HR panel ID: {} is valid - no update needed", panel.getId());
                        return panel;
                }

                logger.info("No existing HR panel found - creating new HR panel for hr@company.com");
                Panel hrPanel = new Panel();
                hrPanel.setName(hrUser.getName() != null ? hrUser.getName() : "HR");
                hrPanel.setEmail(hrUser.getEmail());
                hrPanel.setMobile("HR-" + hrUser.getId());
                hrPanel.setOrganization("Company");
                hrPanel.setDesignation("HR");
                hrPanel.setActive(true);
                hrPanel.setUser(hrUser);

                logger.debug("Saving new HR panel to database for email: hr@company.com");
                Panel savedHrPanel = panelRepository.save(hrPanel);
                logger.info("New HR panel created and saved with ID: {}", savedHrPanel.getId());

                return savedHrPanel;
        }

}