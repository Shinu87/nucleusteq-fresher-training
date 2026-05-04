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

                Interview interview = interviewRepository.findById(request.getInterviewId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                FeedbackMessages.INTERVIEW_NOT_FOUND + " with id: "
                                                                + request.getInterviewId()));

                boolean interviewDone = interview.getStatus() == InterviewStatus.COMPLETED
                                || interview.getScheduledAt().isBefore(LocalDateTime.now());

                if (!interviewDone) {
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

                Panel panel = panelRepository.findById(request.getPanelId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                PanelMessages.NOT_FOUND + request.getPanelId()));

                boolean isAssigned = interview.getPanels().stream()
                                .anyMatch(p -> p.getId().equals(request.getPanelId()));

                if (!isAssigned) {
                        throw new BadRequestException(
                                        FeedbackMessages.PANEL_NOT_ASSIGNED_TO_INTERVIEW);
                }

                if (feedbackRepository.existsByInterviewIdAndPanelId(
                                request.getInterviewId(),
                                request.getPanelId())) {

                        throw new ConflictException(
                                        FeedbackMessages.FEEDBACK_ALREADY_SUBMITTED_BY_PANEL);
                }

                Feedback feedback = new Feedback();
                feedback.setComments(request.getComments());
                feedback.setStrengths(request.getStrengths());
                feedback.setWeaknesses(request.getWeaknesses());
                feedback.setAreasCovered(request.getAreasCovered());
                feedback.setRating(request.getRating());
                feedback.setStatus(request.getStatus());
                feedback.setInterview(interview);
                feedback.setPanel(panel);

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

                return feedbackRepository.findByInterviewId(interviewId)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
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

                return feedbackRepository.findByCandidateId(candidateId)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
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

                return feedbackRepository.existsByInterviewIdAndPanelId(interviewId, panelId);
        }

        /**
         * Maps Feedback entity to FeedbackResponseDTO.
         *
         * @param feedback feedback entity
         * @return mapped response DTO
         */
        private FeedbackResponseDTO mapToResponse(Feedback feedback) {

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
