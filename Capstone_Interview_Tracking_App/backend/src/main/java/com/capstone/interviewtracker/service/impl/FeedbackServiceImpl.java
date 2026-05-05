package com.capstone.interviewtracker.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.capstone.interviewtracker.constants.messages.FeedbackMessages;
import com.capstone.interviewtracker.constants.messages.PanelMessages;
import com.capstone.interviewtracker.dto.Request.FeedbackRequestDTO;
import com.capstone.interviewtracker.dto.Response.FeedbackResponseDTO;
import com.capstone.interviewtracker.enums.InterviewStatus;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.exception.custom.BadRequestException;
import com.capstone.interviewtracker.exception.custom.ConflictException;
import com.capstone.interviewtracker.model.Feedback;
import com.capstone.interviewtracker.model.Interview;
import com.capstone.interviewtracker.model.Panel;
import com.capstone.interviewtracker.repository.FeedbackRepository;
import com.capstone.interviewtracker.repository.InterviewRepository;
import com.capstone.interviewtracker.repository.PanelRepository;
import com.capstone.interviewtracker.repository.UserRepository;
import com.capstone.interviewtracker.service.FeedbackService;

/**
 * Service implementation for Feedback operations.
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

        private static final Logger logger = LoggerFactory.getLogger(FeedbackServiceImpl.class);

        private final FeedbackRepository feedbackRepository;
        private final InterviewRepository interviewRepository;
        private final PanelRepository panelRepository;
        private final UserRepository userRepository;

        /**
         * Constructor injection for FeedbackService dependencies.
         *
         * @param feedbackRepository  feedback repository
         * @param interviewRepository interview repository
         * @param panelRepository     panel repository
         * @param userRepository      user repository
         */
        public FeedbackServiceImpl(
                        FeedbackRepository feedbackRepository,
                        InterviewRepository interviewRepository,
                        PanelRepository panelRepository,
                        UserRepository userRepository) {

                this.feedbackRepository = feedbackRepository;
                this.interviewRepository = interviewRepository;
                this.panelRepository = panelRepository;
                this.userRepository = userRepository;
        }

        /**
         * Panel submits feedback for an interview.
         * Feedback is allowed only after interview completion time.
         * After submission, the interview is marked COMPLETED if needed.
         */
        @Override
        public FeedbackResponseDTO submitFeedback(FeedbackRequestDTO request) {

                logger.info("Submitting feedback for interview id: {} by panel id: {}",
                                request.getInterviewId(), request.getPanelId());

                logger.debug("Looking up interview in database for ID: {}", request.getInterviewId());
                Interview interview = interviewRepository.findById(request.getInterviewId())
                                .orElseThrow(() -> {
                                        logger.warn("Submit feedback failed - interview not found for ID: {}",
                                                        request.getInterviewId());
                                        return new ResourceNotFoundException(
                                                        FeedbackMessages.INTERVIEW_NOT_FOUND + " with id: "
                                                                        + request.getInterviewId());
                                });

                logger.debug("Interview found - ID: {}, status: {}, scheduledAt: {}",
                                interview.getId(), interview.getStatus(), interview.getScheduledAt());

                boolean interviewDone = interview.getStatus() == InterviewStatus.COMPLETED
                                || interview.getScheduledAt().isBefore(LocalDateTime.now());

                if (!interviewDone) {
                        logger.warn("Submit feedback failed - interview not yet done for ID: {}, scheduledAt: {}",
                                        interview.getId(), interview.getScheduledAt());
                        throw new BadRequestException(
                                        FeedbackMessages.FEEDBACK_ONLY_AFTER_INTERVIEW_COMPLETION
                                                        + " Scheduled at: " + interview.getScheduledAt());
                }

                if (interview.getStatus() != InterviewStatus.COMPLETED) {
                        interview.setStatus(InterviewStatus.COMPLETED);
                        interviewRepository.save(interview);

                        logger.info("Auto-marked interview {} as COMPLETED",
                                        interview.getId());
                }

                logger.debug("Looking up panel in database for ID: {}", request.getPanelId());
                Panel panel = panelRepository.findById(request.getPanelId())
                                .orElseThrow(() -> {
                                        logger.warn("Submit feedback failed - panel not found for ID: {}",
                                                        request.getPanelId());
                                        return new ResourceNotFoundException(
                                                        PanelMessages.NOT_FOUND + request.getPanelId());
                                });

                logger.debug("Checking if panelId: {} is assigned to interviewId: {}",
                                request.getPanelId(), request.getInterviewId());
                boolean isAssigned = interview.getPanels().stream()
                                .anyMatch(p -> p.getId().equals(request.getPanelId()));

                if (!isAssigned) {
                        logger.warn("Submit feedback failed - panelId: {} is not assigned to interviewId: {}",
                                        request.getPanelId(), request.getInterviewId());
                        throw new BadRequestException(
                                        FeedbackMessages.PANEL_NOT_ASSIGNED_TO_INTERVIEW);
                }

                logger.debug("Checking if feedback already submitted by panelId: {} for interviewId: {}",
                                request.getPanelId(), request.getInterviewId());
                if (feedbackRepository.existsByInterviewIdAndPanelId(
                                request.getInterviewId(),
                                request.getPanelId())) {
                        logger.warn("Submit feedback failed - feedback already submitted by panelId: {} for interviewId: {}",
                                        request.getPanelId(), request.getInterviewId());
                        throw new ConflictException(
                                        FeedbackMessages.FEEDBACK_ALREADY_SUBMITTED_BY_PANEL);
                }

                logger.debug("Building Feedback object for interviewId: {}, panelId: {}",
                                request.getInterviewId(), request.getPanelId());
                Feedback feedback = new Feedback();
                feedback.setComments(request.getComments());
                feedback.setStrengths(request.getStrengths());
                feedback.setWeaknesses(request.getWeaknesses());
                feedback.setAreasCovered(request.getAreasCovered());
                feedback.setRating(request.getRating());
                feedback.setStatus(request.getStatus());
                feedback.setInterview(interview);
                feedback.setPanel(panel);

                logger.debug("Saving feedback to database for interviewId: {}, panelId: {}",
                                request.getInterviewId(), request.getPanelId());
                Feedback saved = feedbackRepository.save(feedback);

                logger.info("Feedback submitted with id: {}", saved.getId());

                return mapToResponse(saved);
        }

        /**
         * Fetches all feedback entries for a given interview.
         *
         * @param interviewId interview id
         * @return list of feedback responses
         */
        @Override
        public List<FeedbackResponseDTO> getFeedbackByInterview(Long interviewId) {

                logger.info("Fetching feedback for interview id: {}", interviewId);

                logger.debug("Calling feedbackRepository.findByInterviewId() for interviewId: {}", interviewId);

                List<FeedbackResponseDTO> feedbackList = feedbackRepository.findByInterviewId(interviewId)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();

                logger.info("Successfully fetched {} feedback record(s) for interviewId: {}",
                                feedbackList.size(), interviewId);

                return feedbackList;
        }

        /**
         * Fetches all feedback for a given candidate.
         *
         * @param candidateId candidate id
         * @return list of feedback responses
         */
        @Override
        public List<FeedbackResponseDTO> getFeedbackByCandidate(Long candidateId) {

                logger.info("Fetching all feedback for candidate id: {}", candidateId);

                logger.debug("Calling feedbackRepository.findByCandidateId() for candidateId: {}", candidateId);

                List<FeedbackResponseDTO> feedbackList = feedbackRepository.findByCandidateId(candidateId)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();

                logger.info("Successfully fetched {} feedback record(s) for candidateId: {}",
                                feedbackList.size(), candidateId);

                return feedbackList;
        }

        /**
         * Checks whether a panel has already submitted feedback
         * for a given interview.
         *
         * @param interviewId interview id
         * @param panelId     panel id
         * @return true if feedback exists, false otherwise
         */
        @Override
        public boolean hasFeedbackSubmitted(Long interviewId, Long panelId) {

                logger.debug("Checking if feedback already submitted by panelId: {} for interviewId: {}",
                                panelId, interviewId);

                boolean exists = feedbackRepository.existsByInterviewIdAndPanelId(interviewId, panelId);

                logger.debug("Feedback submitted check result - interviewId: {}, panelId: {}, exists: {}",
                                interviewId, panelId, exists);

                return exists;
        }

        /**
         * Maps Feedback entity to FeedbackResponseDTO.
         *
         * @param feedback feedback entity
         * @return mapped response DTO
         */
        private FeedbackResponseDTO mapToResponse(Feedback feedback) {

                logger.debug("Mapping feedback entity to response DTO for feedbackId: {}", feedback.getId());

                FeedbackResponseDTO response = new FeedbackResponseDTO();
                response.setId(feedback.getId());
                response.setComments(feedback.getComments());
                response.setStrengths(feedback.getStrengths());
                response.setWeaknesses(feedback.getWeaknesses());
                response.setAreasCovered(feedback.getAreasCovered());
                response.setRating(feedback.getRating());
                response.setStatus(feedback.getStatus());
                response.setInterviewId(feedback.getInterview().getId());
                response.setPanelId(feedback.getPanel().getId());
                response.setPanelName(feedback.getPanel().getName());
                response.setInterviewStage(feedback.getInterview().getStage());

                return response;
        }
}