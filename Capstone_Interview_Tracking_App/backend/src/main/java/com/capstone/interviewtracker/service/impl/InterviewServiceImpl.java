package com.capstone.interviewtracker.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        CandidateMessages.CANDIDATE_NOT_FOUND + " with id: " + request.getCandidateId()));

        if (candidate.getStatus() == CandidateStatus.REJECTED) {
            throw new BadRequestException(
                    InterviewMessages.CANNOT_SCHEDULE_INTERVIEW_FOR_REJECTED_CANDIDATE);
        }

        if (candidate.getStatus() == CandidateStatus.SELECTED) {
            throw new ConflictException(
                    InterviewMessages.CANNOT_SCHEDULE_INTERVIEW_FOR_SELECTED_CANDIDATE);
        }

        if (request.getScheduledAt() == null
                || request.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(
                    InterviewMessages.SCHEDULED_TIME_MUST_BE_IN_FUTURE);
        }

        if (candidate.getApplicationId() == null) {
            candidate.setApplicationId(1);
            candidate = candidateRepository.save(candidate);
        }

        Stage requestedStage = request.getStage();
        Stage currentStage = candidate.getCurrentStage();

        switch (requestedStage) {

            case SCREENING:
                if (currentStage != Stage.PROFILING
                        && currentStage != Stage.SCREENING) {
                    throw new BadRequestException(
                            InterviewMessages.CANNOT_SCHEDULE_SCREENING_BEFORE_PROFILING_COMPLETION
                                    + " Current stage: " + currentStage);
                }
                break;

            case L1:
                if (currentStage == Stage.PROFILING) {
                    throw new BadRequestException(
                            InterviewMessages.CANNOT_SCHEDULE_L1_BEFORE_SCREENING_COMPLETION
                                    + " Current stage: " + currentStage);
                }
                break;

            case L2:
                Optional<Interview> l1Interview = interviewRepository.findByCandidateIdAndApplicationIdAndStage(
                        request.getCandidateId(),
                        candidate.getApplicationId(),
                        Stage.L1);

                if (l1Interview.isEmpty()) {
                    throw new ResourceNotFoundException(
                            InterviewMessages.L1_INTERVIEW_NOT_SCHEDULED);
                }

                if (l1Interview.get().getStatus() != InterviewStatus.COMPLETED) {
                    throw new BadRequestException(
                            InterviewMessages.L1_MUST_BE_COMPLETED_BEFORE_L2
                                    + " Status: " + l1Interview.get().getStatus());
                }
                break;

            case HR:
                Optional<Interview> l2Interview = interviewRepository.findByCandidateIdAndApplicationIdAndStage(
                        request.getCandidateId(),
                        candidate.getApplicationId(),
                        Stage.L2);

                if (l2Interview.isEmpty()) {
                    throw new ResourceNotFoundException(
                            InterviewMessages.L2_INTERVIEW_NOT_SCHEDULED);
                }

                if (l2Interview.get().getStatus() != InterviewStatus.COMPLETED) {
                    throw new BadRequestException(
                            InterviewMessages.L2_MUST_BE_COMPLETED_BEFORE_HR
                                    + " Status: " + l2Interview.get().getStatus());
                }
                break;

            default:
                break;
        }

        if (interviewRepository.existsByCandidateIdAndApplicationIdAndStage(
                request.getCandidateId(),
                candidate.getApplicationId(),
                requestedStage)) {

            throw new ConflictException(
                    InterviewMessages.INTERVIEW_ALREADY_EXISTS_FOR_STAGE);
        }

        List<Panel> panels;

        if (requestedStage == Stage.HR) {
            Panel hrPanel = resolveOrCreateHrPanel();
            panels = List.of(hrPanel);

        } else {
            List<Long> panelIds = request.getPanelIds();

            if (panelIds == null
                    || panelIds.isEmpty()
                    || panelIds.size() > 2) {
                throw new BadRequestException(
                        InterviewMessages.PANEL_ASSIGNMENT_MUST_BE_BETWEEN_1_AND_2);
            }

            panels = panelIds.stream().map(panelId -> {
                Panel panel = panelRepository.findById(panelId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                PanelMessages.NOT_FOUND + panelId));

                if (!panel.isActive()) {
                    throw new BadRequestException(
                            PanelMessages.PANEL_NOT_ACTIVE + panelId);
                }

                return panel;
            }).toList();
        }

        Interview interview = new Interview();
        interview.setStage(requestedStage);
        interview.setScheduledAt(request.getScheduledAt());
        interview.setFocusArea(request.getFocusArea());
        interview.setMeetingUrl(request.getMeetingUrl());
        interview.setCandidate(candidate);
        interview.setPanels(panels);
        interview.setStatus(InterviewStatus.SCHEDULED);
        interview.setApplicationId(candidate.getApplicationId());

        Interview saved = interviewRepository.save(interview);

        for (Panel panel : panels) {
            emailService.sendInterviewAssignmentEmail(panel, saved);
        }

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

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        InterviewMessages.INTERVIEW_NOT_FOUND + " with id: " + interviewId));

        if (interview.getStatus() == InterviewStatus.COMPLETED) {
            throw new ConflictException(
                    InterviewMessages.INTERVIEW_ALREADY_COMPLETED);
        }

        if (interview.getStatus() == InterviewStatus.CANCELLED) {
            throw new BadRequestException(
                    InterviewMessages.CANNOT_COMPLETE_CANCELLED_INTERVIEW);
        }

        interview.setStatus(InterviewStatus.COMPLETED);

        Interview saved = interviewRepository.save(interview);

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

        List<Interview> all = interviewRepository.findAllWithPanels();

        promoteScheduledToOngoing(all);

        return all.stream()
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

        if (!candidateRepository.existsById(candidateId)) {
            throw new ResourceNotFoundException(
                    CandidateMessages.CANDIDATE_NOT_FOUND + " with id: " + candidateId);
        }

        List<Interview> interviews = interviewRepository.findByCandidateIdWithPanels(candidateId);

        promoteScheduledToOngoing(interviews);

        return interviews.stream()
                .map(this::mapToResponse)
                .toList();
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

        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        InterviewMessages.INTERVIEW_NOT_FOUND + " with id: " + id));

        if (interview.getStatus() == InterviewStatus.SCHEDULED
                && interview.getScheduledAt().isBefore(LocalDateTime.now())) {

            interview.setStatus(InterviewStatus.ONGOING);
            interviewRepository.save(interview);
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

        interviews.forEach(interview -> {
            if (interview.getStatus() == InterviewStatus.SCHEDULED
                    && interview.getScheduledAt().isBefore(now)) {

                interview.setStatus(InterviewStatus.ONGOING);
                interviewRepository.save(interview);
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

        User hrUser = userRepository.findByEmail("hr@company.com")
                .orElseThrow(() -> new ResourceNotFoundException(
                        InterviewMessages.HR_USER_NOT_FOUND_CANNOT_SCHEDULE_INTERVIEW));

        if (hrUser.getRole() != Role.HR) {
            throw new UnauthorizedException(
                    InterviewMessages.CONFIGURED_HR_ACCOUNT_NOT_IN_HR_ROLE);
        }
        Optional<Panel> existing = panelRepository.findByEmail(hrUser.getEmail());

        if (existing.isPresent()) {
            Panel panel = existing.get();

            boolean updated = false;

            if (!panel.isActive()) {
                panel.setActive(true);
                updated = true;
            }

            if (panel.getUser() == null
                    || !panel.getUser().getId().equals(hrUser.getId())) {
                panel.setUser(hrUser);
                updated = true;
            }

            return updated ? panelRepository.save(panel) : panel;
        }

        Panel hrPanel = new Panel();
        hrPanel.setName(hrUser.getName() != null ? hrUser.getName() : "HR");
        hrPanel.setEmail(hrUser.getEmail());
        hrPanel.setMobile("HR-" + hrUser.getId());
        hrPanel.setOrganization("Company");
        hrPanel.setDesignation("HR");
        hrPanel.setActive(true);
        hrPanel.setUser(hrUser);

        return panelRepository.save(hrPanel);
    }

}