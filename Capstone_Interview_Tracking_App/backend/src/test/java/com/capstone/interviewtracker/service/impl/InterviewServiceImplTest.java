package com.capstone.interviewtracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.capstone.interviewtracker.dto.Request.InterviewRequestDTO;
import com.capstone.interviewtracker.dto.Response.InterviewResponseDTO;
import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.enums.InterviewStatus;
import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.enums.Stage;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.model.Candidate;
import com.capstone.interviewtracker.model.Interview;
import com.capstone.interviewtracker.model.JobDescription;
import com.capstone.interviewtracker.model.Panel;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.CandidateRepository;
import com.capstone.interviewtracker.repository.InterviewRepository;
import com.capstone.interviewtracker.repository.PanelRepository;
import com.capstone.interviewtracker.repository.UserRepository;
import com.capstone.interviewtracker.service.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for InterviewServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class InterviewServiceImplTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private PanelRepository panelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private InterviewServiceImpl interviewService;

    private Candidate candidate;
    private Panel panel;
    private InterviewRequestDTO request;
    private Interview interview;

    /**
     * Sets up common test data before each test.
     */
    @BeforeEach
    void setUp() {
        JobDescription job = new JobDescription();
        job.setTitle("Java Backend Developer");

        candidate = new Candidate();
        candidate.setId(11L);
        candidate.setName("Amit Sharma");
        candidate.setStatus(CandidateStatus.IN_PROGRESS);
        candidate.setCurrentStage(Stage.PROFILING);
        candidate.setApplicationId(1);
        candidate.setJobDescription(job);

        panel = new Panel();
        panel.setId(2L);
        panel.setName("Rahul Patil");
        panel.setEmail("rahul.patil@tcs.com");
        panel.setActive(true);

        request = new InterviewRequestDTO();
        request.setStage(Stage.SCREENING);
        request.setScheduledAt(LocalDateTime.now().plusDays(1));
        request.setFocusArea("Spring Boot Microservices");
        request.setMeetingUrl("https://meet.google.com/ind-abc-123");
        request.setCandidateId(11L);
        request.setPanelIds(List.of(2L));

        interview = new Interview();
        interview.setId(99L);
        interview.setStage(Stage.SCREENING);
        interview.setScheduledAt(request.getScheduledAt());
        interview.setCandidate(candidate);
        interview.setPanels(List.of(panel));
        interview.setStatus(InterviewStatus.SCHEDULED);
    }

    /**
     * Tests scheduling an interview successfully for SCREENING stage.
     */
    @Test
    void testScheduleInterviewSuccess() {
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.existsByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.SCREENING))
                .thenReturn(false);
        when(panelRepository.findById(2L)).thenReturn(Optional.of(panel));
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);
        doNothing().when(emailService).sendInterviewAssignmentEmail(any(Panel.class), any(Interview.class));

        InterviewResponseDTO response = interviewService.scheduleInterview(request);

        assertNotNull(response);
        assertEquals(99L, response.getId());
    }

    /**
     * Tests scheduling when candidate is not found.
     */
    @Test
    void testScheduleInterviewCandidateNotFound() {
        when(candidateRepository.findById(11L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that a rejected candidate cannot be scheduled.
     */
    @Test
    void testScheduleInterviewRejectedCandidate() {
        candidate.setStatus(CandidateStatus.REJECTED);
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that a selected candidate cannot be scheduled.
     */
    @Test
    void testScheduleInterviewSelectedCandidate() {
        candidate.setStatus(CandidateStatus.SELECTED);
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that scheduling with a past time fails.
     */
    @Test
    void testScheduleInterviewPastTime() {
        request.setScheduledAt(LocalDateTime.now().minusHours(1));
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that scheduling with a null time fails.
     */
    @Test
    void testScheduleInterviewNullTime() {
        request.setScheduledAt(null);
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that L1 cannot be scheduled if candidate is still in PROFILING.
     */
    @Test
    void testScheduleL1BeforeScreening() {
        request.setStage(Stage.L1);
        candidate.setCurrentStage(Stage.PROFILING);
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that L2 cannot be scheduled when no L1 exists.
     */
    @Test
    void testScheduleL2WithoutL1() {
        request.setStage(Stage.L2);
        candidate.setCurrentStage(Stage.L1);
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.L1))
                .thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that L2 cannot be scheduled if L1 is not completed.
     */
    @Test
    void testScheduleL2WhenL1NotCompleted() {
        request.setStage(Stage.L2);
        candidate.setCurrentStage(Stage.L1);
        Interview l1 = new Interview();
        l1.setStatus(InterviewStatus.SCHEDULED);
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.L1))
                .thenReturn(Optional.of(l1));
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that HR cannot be scheduled when no L2 exists.
     */
    @Test
    void testScheduleHrWithoutL2() {
        request.setStage(Stage.HR);
        candidate.setCurrentStage(Stage.L2);
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.L2))
                .thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that HR cannot be scheduled if L2 is not completed.
     */
    @Test
    void testScheduleHrWhenL2NotCompleted() {
        request.setStage(Stage.HR);
        candidate.setCurrentStage(Stage.L2);
        Interview l2 = new Interview();
        l2.setStatus(InterviewStatus.SCHEDULED);
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.L2))
                .thenReturn(Optional.of(l2));
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that duplicate interview at the same stage is not allowed.
     */
    @Test
    void testScheduleDuplicateInterview() {
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.existsByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.SCREENING)).thenReturn(true);
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that an empty list of panel ids fails.
     */
    @Test
    void testScheduleEmptyPanelIds() {
        request.setPanelIds(List.of());
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.existsByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.SCREENING))
                .thenReturn(false);
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that null panel ids fails.
     */
    @Test
    void testScheduleNullPanelIds() {
        request.setPanelIds(null);
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.existsByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.SCREENING))
                .thenReturn(false);
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests scheduling when panel is not found.
     */
    @Test
    void testSchedulePanelNotFound() {
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.existsByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.SCREENING))
                .thenReturn(false);
        when(panelRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests scheduling fails when panel is inactive.
     */
    @Test
    void testScheduleInactivePanel() {
        panel.setActive(false);
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.existsByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.SCREENING))
                .thenReturn(false);
        when(panelRepository.findById(2L)).thenReturn(Optional.of(panel));
        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that HR stage uses HR panel automatically.
     */
    @Test
    void testScheduleHrUsesHrPanel() {
        request.setStage(Stage.HR);
        candidate.setCurrentStage(Stage.L2);

        Interview completedL2 = new Interview();
        completedL2.setStatus(InterviewStatus.COMPLETED);

        User hrUser = new User();
        org.springframework.test.util.ReflectionTestUtils.setField(hrUser, "id", 99L);
        hrUser.setName("HR Admin");
        hrUser.setEmail("hr@company.com");
        hrUser.setRole(Role.HR);

        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.L2))
                .thenReturn(Optional.of(completedL2));
        when(interviewRepository.existsByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.HR)).thenReturn(false);
        when(userRepository.findByEmail("hr@company.com")).thenReturn(Optional.of(hrUser));
        when(panelRepository.findByEmail("hr@company.com")).thenReturn(Optional.empty());
        when(panelRepository.save(any(Panel.class))).thenAnswer(inv -> {
            Panel p = inv.getArgument(0);
            p.setId(500L);
            return p;
        });
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> {
            Interview i = inv.getArgument(0);
            i.setId(200L);
            return i;
        });
        doNothing().when(emailService).sendInterviewAssignmentEmail(any(Panel.class), any(Interview.class));

        InterviewResponseDTO response = interviewService.scheduleInterview(request);
        assertNotNull(response);
    }

    /**
     * Tests that HR stage fails when no HR user is present.
     */
    @Test
    void testScheduleHrNoHrUser() {
        request.setStage(Stage.HR);
        candidate.setCurrentStage(Stage.L2);

        Interview completedL2 = new Interview();
        completedL2.setStatus(InterviewStatus.COMPLETED);

        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.L2))
                .thenReturn(Optional.of(completedL2));
        when(interviewRepository.existsByCandidateIdAndApplicationIdAndStage(11L, 1, Stage.HR)).thenReturn(false);
        when(userRepository.findByEmail("hr@company.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> interviewService.scheduleInterview(request));
    }

    /**
     * Tests that an applicationId is auto-assigned if missing.
     */
    @Test
    void testScheduleSetsApplicationIdIfMissing() {
        candidate.setApplicationId(null);
        when(candidateRepository.findById(11L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(inv -> inv.getArgument(0));
        when(interviewRepository.existsByCandidateIdAndApplicationIdAndStage(anyLong(), any(), any()))
                .thenReturn(false);
        when(panelRepository.findById(2L)).thenReturn(Optional.of(panel));
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);
        doNothing().when(emailService).sendInterviewAssignmentEmail(any(Panel.class), any(Interview.class));

        InterviewResponseDTO response = interviewService.scheduleInterview(request);
        assertNotNull(response);
    }

    /**
     * Tests marking an interview as completed successfully.
     */
    @Test
    void testMarkCompletedSuccess() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> inv.getArgument(0));

        InterviewResponseDTO result = interviewService.markCompleted(99L);
        assertEquals(InterviewStatus.COMPLETED, interview.getStatus());
        assertNotNull(result);
    }

    /**
     * Tests marking completed when interview is not found.
     */
    @Test
    void testMarkCompletedNotFound() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> interviewService.markCompleted(99L));
    }

    /**
     * Tests that already completed interview cannot be marked again.
     */
    @Test
    void testMarkCompletedAlreadyCompleted() {
        interview.setStatus(InterviewStatus.COMPLETED);
        when(interviewRepository.findById(99L)).thenReturn(Optional.of(interview));
        assertThrows(RuntimeException.class, () -> interviewService.markCompleted(99L));
    }

    /**
     * Tests that a cancelled interview cannot be marked completed.
     */
    @Test
    void testMarkCompletedCancelled() {
        interview.setStatus(InterviewStatus.CANCELLED);
        when(interviewRepository.findById(99L)).thenReturn(Optional.of(interview));
        assertThrows(RuntimeException.class, () -> interviewService.markCompleted(99L));
    }

    /**
     * Tests that overdue scheduled interviews are auto-promoted to ONGOING.
     */
    @Test
    void testGetAllInterviewsPromotesOverdue() {
        Interview overdue = new Interview();
        overdue.setId(50L);
        overdue.setStage(Stage.L1);
        overdue.setStatus(InterviewStatus.SCHEDULED);
        overdue.setScheduledAt(LocalDateTime.now().minusHours(1));
        overdue.setCandidate(candidate);
        overdue.setPanels(List.of(panel));

        when(interviewRepository.findAllWithPanels()).thenReturn(List.of(overdue));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> inv.getArgument(0));

        List<InterviewResponseDTO> list = interviewService.getAllInterviews();
        assertEquals(1, list.size());
        assertEquals(InterviewStatus.ONGOING, overdue.getStatus());
    }

    /**
     * Tests that fetching interviews by an unknown candidate id throws.
     */
    @Test
    void testGetInterviewsByCandidateNotFound() {
        when(candidateRepository.existsById(11L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> interviewService.getInterviewsByCandidate(11L));
    }

    /**
     * Tests fetching interviews by candidate id successfully.
     */
    @Test
    void testGetInterviewsByCandidateSuccess() {
        when(candidateRepository.existsById(11L)).thenReturn(true);
        when(interviewRepository.findByCandidateIdWithPanels(11L)).thenReturn(List.of(interview));
        List<InterviewResponseDTO> list = interviewService.getInterviewsByCandidate(11L);
        assertEquals(1, list.size());
    }

    /**
     * Tests fetching an interview by id successfully.
     */
    @Test
    void testGetInterviewByIdSuccess() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.of(interview));
        InterviewResponseDTO res = interviewService.getInterviewById(99L);
        assertNotNull(res);
        assertEquals(99L, res.getId());
    }

    /**
     * Tests that overdue interview is promoted to ONGOING when fetched by id.
     */
    @Test
    void testGetInterviewByIdPromotesOverdue() {
        interview.setScheduledAt(LocalDateTime.now().minusHours(1));
        interview.setStatus(InterviewStatus.SCHEDULED);
        when(interviewRepository.findById(99L)).thenReturn(Optional.of(interview));
        when(interviewRepository.save(any(Interview.class))).thenAnswer(inv -> inv.getArgument(0));

        interviewService.getInterviewById(99L);
        assertEquals(InterviewStatus.ONGOING, interview.getStatus());
    }

    /**
     * Tests fetching an interview by id when not found.
     */
    @Test
    void testGetInterviewByIdNotFound() {
        when(interviewRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> interviewService.getInterviewById(99L));
    }
}