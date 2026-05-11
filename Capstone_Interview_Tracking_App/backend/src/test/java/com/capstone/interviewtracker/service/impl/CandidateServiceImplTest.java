package com.capstone.interviewtracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.capstone.interviewtracker.dto.Request.CandidateRequestDTO;
import com.capstone.interviewtracker.dto.Response.ApplicationStatusDTO;
import com.capstone.interviewtracker.dto.Response.CandidateResponseDTO;
import com.capstone.interviewtracker.enums.CandidateStatus;
import com.capstone.interviewtracker.enums.InterviewStatus;
import com.capstone.interviewtracker.enums.Role;
import com.capstone.interviewtracker.enums.Stage;
import com.capstone.interviewtracker.exception.custom.BadRequestException;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.model.Candidate;
import com.capstone.interviewtracker.model.Feedback;
import com.capstone.interviewtracker.model.Interview;
import com.capstone.interviewtracker.model.JobDescription;
import com.capstone.interviewtracker.model.Panel;
import com.capstone.interviewtracker.model.User;
import com.capstone.interviewtracker.repository.CandidateRepository;
import com.capstone.interviewtracker.repository.FeedbackRepository;
import com.capstone.interviewtracker.repository.InterviewRepository;
import com.capstone.interviewtracker.repository.JobDescriptionRepository;
import com.capstone.interviewtracker.repository.UserRepository;
import com.capstone.interviewtracker.service.EmailService;
import com.capstone.interviewtracker.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for CandidateServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {

    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private JobDescriptionRepository jobDescriptionRepository;
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    private CandidateRequestDTO request;
    private JobDescription job;
    private Candidate existingCandidate;

    /**
     * Sets up common test data before each test.
     */
    @BeforeEach
    void setUp() {
        job = new JobDescription();
        job.setTitle("SDE");
        job.setActive(true);

        /* setting id using reflection because there is no setter */
        try {
            var f = JobDescription.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(job, 10L);
        } catch (Exception ignored) {
        }

        request = new CandidateRequestDTO();
        request.setName("Rahul");
        request.setEmail("Rahul@example.com");
        request.setPhone("9876543210");
        request.setDateOfBirth(LocalDate.of(1997, 6, 15));
        request.setTotalExperience(5);
        request.setRelevantExperience(3);
        request.setCurrentStage(Stage.PROFILING);
        request.setStatus(CandidateStatus.IN_PROGRESS);
        request.setJobId(10L);

        existingCandidate = new Candidate();
        existingCandidate.setId(100L);
        existingCandidate.setName("Rahul");
        existingCandidate.setEmail("Rahul@example.com");
        existingCandidate.setPhone("9876543210");
        existingCandidate.setDateOfBirth(LocalDate.of(1997, 6, 15));
        existingCandidate.setTotalExperience(5);
        existingCandidate.setStatus(CandidateStatus.IN_PROGRESS);
        existingCandidate.setCurrentStage(Stage.PROFILING);
        existingCandidate.setJobDescription(job);
        existingCandidate.setApplicationId(1);
    }

    /**
     * Tests creating a new candidate with valid data.
     */
    @Test
    void testCreateCandidateSuccess() {
        when(candidateRepository.findActiveByEmail(anyString(), any())).thenReturn(List.of());
        when(candidateRepository.findActiveByPhone(anyString(), any())).thenReturn(List.of());
        when(jobDescriptionRepository.findById(10L)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(candidateRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(inv -> {
            Candidate c = inv.getArgument(0);
            c.setId(100L);
            return c;
        });
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            org.springframework.test.util.ReflectionTestUtils.setField(u, "id", 50L);
            return u;
        });
        when(userService.createTokenAndBuildLink(anyString(), anyString())).thenReturn("http://link");

        CandidateResponseDTO response = candidateService.createCandidate(request);

        assertNotNull(response);
        assertEquals("rahul@example.com", response.getEmail());
    }

    /**
     * Tests that duplicate email throws an exception.
     */
    @Test
    void testCreateCandidateDuplicateEmail() {
        when(candidateRepository.findActiveByEmail(anyString(), any()))
                .thenReturn(List.of(existingCandidate));
        assertThrows(RuntimeException.class, () -> candidateService.createCandidate(request));
    }

    /**
     * Tests that duplicate phone throws an exception.
     */
    @Test
    void testCreateCandidateDuplicatePhone() {
        when(candidateRepository.findActiveByEmail(anyString(), any())).thenReturn(List.of());
        when(candidateRepository.findActiveByPhone(anyString(), any()))
                .thenReturn(List.of(existingCandidate));
        assertThrows(RuntimeException.class, () -> candidateService.createCandidate(request));
    }

    /**
     * Tests that creating a candidate fails when the job is not found.
     */
    @Test
    void testCreateCandidateJobNotFound() {
        when(candidateRepository.findActiveByEmail(anyString(), any())).thenReturn(List.of());
        when(candidateRepository.findActiveByPhone(anyString(), any())).thenReturn(List.of());
        when(jobDescriptionRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> candidateService.createCandidate(request));
    }

    /**
     * Tests that an inactive job cannot be applied for.
     */
    @Test
    void testCreateCandidateInactiveJob() {
        job.setActive(false);
        when(candidateRepository.findActiveByEmail(anyString(), any())).thenReturn(List.of());
        when(candidateRepository.findActiveByPhone(anyString(), any())).thenReturn(List.of());
        when(jobDescriptionRepository.findById(10L)).thenReturn(Optional.of(job));
        assertThrows(RuntimeException.class, () -> candidateService.createCandidate(request));
    }

    /**
     * Tests that missing date of birth gives a bad request error.
     */
    @Test
    void testCreateCandidateMissingDob() {
        request.setDateOfBirth(null);
        when(candidateRepository.findActiveByEmail(anyString(), any())).thenReturn(List.of());
        when(candidateRepository.findActiveByPhone(anyString(), any())).thenReturn(List.of());
        when(jobDescriptionRepository.findById(10L)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> candidateService.createCandidate(request));
    }

    /**
     * Tests that candidate below 18 years is rejected.
     */
    @Test
    void testCreateCandidateUnderAge() {
        request.setDateOfBirth(LocalDate.now().minusYears(15));
        when(candidateRepository.findActiveByEmail(anyString(), any())).thenReturn(List.of());
        when(candidateRepository.findActiveByPhone(anyString(), any())).thenReturn(List.of());
        when(jobDescriptionRepository.findById(10L)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> candidateService.createCandidate(request));
    }

    /**
     * Tests that candidate above 60 years is rejected.
     */
    @Test
    void testCreateCandidateOverAge() {
        request.setDateOfBirth(LocalDate.now().minusYears(70));
        when(candidateRepository.findActiveByEmail(anyString(), any())).thenReturn(List.of());
        when(candidateRepository.findActiveByPhone(anyString(), any())).thenReturn(List.of());
        when(jobDescriptionRepository.findById(10L)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> candidateService.createCandidate(request));
    }

    /**
     * Tests that a rejected candidate can re-apply for a job.
     */
    @Test
    void testReapplyForRejectedCandidate() {
        existingCandidate.setStatus(CandidateStatus.REJECTED);
        when(candidateRepository.findActiveByEmail(anyString(), any())).thenReturn(List.of());
        when(candidateRepository.findActiveByPhone(anyString(), any())).thenReturn(List.of());
        when(jobDescriptionRepository.findById(10L)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(candidateRepository.findByEmail(anyString())).thenReturn(Optional.of(existingCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(userService.createTokenAndBuildLink(anyString(), anyString())).thenReturn("http://link");

        CandidateResponseDTO response = candidateService.createCandidate(request);
        assertNotNull(response);
    }

    /**
     * Tests that an existing non-rejected candidate cannot apply again.
     */
    @Test
    void testCreateCandidateAlreadyExistsNotRejected() {
        existingCandidate.setStatus(CandidateStatus.IN_PROGRESS);
        when(candidateRepository.findActiveByEmail(anyString(), any())).thenReturn(List.of());
        when(candidateRepository.findActiveByPhone(anyString(), any())).thenReturn(List.of());
        when(jobDescriptionRepository.findById(10L)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(candidateRepository.findByEmail(anyString())).thenReturn(Optional.of(existingCandidate));

        assertThrows(RuntimeException.class, () -> candidateService.createCandidate(request));
    }

    /**
     * Tests that DOB from linked user is used when request DOB is null.
     */
    @Test
    void testCreateCandidateUsesUserDob() {
        request.setDateOfBirth(null);
        User user = new User();
        org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 50L);
        user.setEmail("Rahul@example.com");
        user.setDateOfBirth(LocalDate.now().minusYears(30));

        when(candidateRepository.findActiveByEmail(anyString(), any())).thenReturn(List.of());
        when(candidateRepository.findActiveByPhone(anyString(), any())).thenReturn(List.of());
        when(jobDescriptionRepository.findById(10L)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(50L)).thenReturn(Optional.empty());
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(inv -> {
            Candidate c = inv.getArgument(0);
            c.setId(100L);
            return c;
        });

        CandidateResponseDTO response = candidateService.createCandidate(request);
        assertNotNull(response);
    }

    /**
     * Tests successful re-apply with a new job.
     */
    @Test
    void testReApplySuccess() {
        existingCandidate.setStatus(CandidateStatus.REJECTED);
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        when(jobDescriptionRepository.findById(11L)).thenReturn(Optional.of(job));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidateResponseDTO response = candidateService.reApply(100L, 11L, existingCandidate.getEmail());
        assertNotNull(response);
        assertEquals(Stage.PROFILING, existingCandidate.getCurrentStage());
        assertEquals(CandidateStatus.IN_PROGRESS, existingCandidate.getStatus());
    }

    /**
     * Tests re-apply when candidate id is not found.
     */
    @Test
    void testReApplyCandidateNotFound() {
        when(candidateRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.reApply(100L, 11L, existingCandidate.getEmail()));
    }

    /**
     * Tests that only rejected candidates can re-apply.
     */
    @Test
    void testReApplyNotRejected() {
        existingCandidate.setStatus(CandidateStatus.IN_PROGRESS);
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        assertThrows(RuntimeException.class, () -> candidateService.reApply(100L, 11L, existingCandidate.getEmail()));
    }

    /**
     * Tests re-apply when the new job is not found.
     */
    @Test
    void testReApplyJobNotFound() {
        existingCandidate.setStatus(CandidateStatus.REJECTED);
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        when(jobDescriptionRepository.findById(11L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.reApply(100L, 11L, existingCandidate.getEmail()));
    }

    /**
     * Tests re-apply when the new job is inactive.
     */
    @Test
    void testReApplyInactiveJob() {
        existingCandidate.setStatus(CandidateStatus.REJECTED);
        job.setActive(false);
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        when(jobDescriptionRepository.findById(11L)).thenReturn(Optional.of(job));
        assertThrows(RuntimeException.class, () -> candidateService.reApply(100L, 11L, existingCandidate.getEmail()));
    }

    /**
     * Tests successful update of candidate's resume path.
     */
    @Test
    void testUpdateResumeSuccess() {
        User candidateUser = new User();
        candidateUser.setEmail("Rahul@example.com");
        candidateUser.setRole(Role.CANDIDATE);

        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        when(userRepository.findByEmail("Rahul@example.com")).thenReturn(Optional.of(candidateUser));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidateResponseDTO response = candidateService.updateResumePath(100L, "uploads/resumes/x.pdf",
                "Rahul@example.com");
        assertEquals("uploads/resumes/x.pdf", response.getResumeUrl());
    }

    /**
     * Tests resume update when candidate is not found.
     */
    @Test
    void testUpdateResumeCandidateNotFound() {
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.updateResumePath(100L, "x", "Rahul@example.com"));
    }

    /**
     * Tests fetching all candidates returns a non-empty list.
     */
    @Test
    void testGetAllCandidates() {
        when(candidateRepository.findAll()).thenReturn(List.of(existingCandidate));
        List<CandidateResponseDTO> result = candidateService.getAllCandidates();
        assertEquals(1, result.size());
    }

    /**
     * Tests fetching a candidate by id.
     */
    @Test
    void testGetCandidateById() {
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        CandidateResponseDTO result = candidateService.getCandidateById(100L);
        assertEquals(100L, result.getId());
    }

    /**
     * Tests that fetching a non-existing candidate throws an exception.
     */
    @Test
    void testGetCandidateByIdNotFound() {
        when(candidateRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> candidateService.getCandidateById(100L));
    }

    /**
     * Tests stage advancement from PROFILING to SCREENING.
     */
    @Test
    void testAdvanceProfilingToScreening() {
        existingCandidate.setCurrentStage(Stage.PROFILING);
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidateResponseDTO response = candidateService.advanceStage(100L);
        assertEquals(Stage.SCREENING, response.getCurrentStage());
    }

    /**
     * Tests stage advancement from SCREENING to L1.
     */
    @Test
    void testAdvanceScreeningToL1() {
        existingCandidate.setCurrentStage(Stage.SCREENING);
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidateResponseDTO response = candidateService.advanceStage(100L);
        assertEquals(Stage.L1, response.getCurrentStage());
    }

    /**
     * Tests stage advancement from L1 to L2 when feedback is given.
     */
    @Test
    void testAdvanceL1ToL2() {
        existingCandidate.setCurrentStage(Stage.L1);

        Panel p = new Panel();
        p.setId(2L);
        Interview iv = new Interview();
        iv.setId(99L);
        iv.setStage(Stage.L1);
        iv.setStatus(InterviewStatus.COMPLETED);
        iv.setPanels(List.of(p));

        Feedback fb = new Feedback();
        fb.setId(1L);

        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        when(interviewRepository.findByCandidateIdAndApplicationIdAndStage(100L, 1, Stage.L1))
                .thenReturn(Optional.of(iv));
        when(feedbackRepository.findByInterviewId(99L)).thenReturn(List.of(fb));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidateResponseDTO response = candidateService.advanceStage(100L);
        assertEquals(Stage.L2, response.getCurrentStage());
    }

    /**
     * Tests that L1 stage cannot advance when feedback is missing.
     */
    @Test
    void testAdvanceL1NoFeedback() {
        existingCandidate.setCurrentStage(Stage.L1);

        Panel p = new Panel();
        p.setId(2L);
        Interview iv = new Interview();
        iv.setId(99L);
        iv.setPanels(List.of(p));

        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        when(interviewRepository.findByCandidateIdAndApplicationIdAndStage(100L, 1, Stage.L1))
                .thenReturn(Optional.of(iv));
        when(feedbackRepository.findByInterviewId(99L)).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> candidateService.advanceStage(100L));
    }

    /**
     * Tests that L1 stage cannot advance when no interview is scheduled.
     */
    @Test
    void testAdvanceL1NoInterview() {
        existingCandidate.setCurrentStage(Stage.L1);
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        when(interviewRepository.findByCandidateIdAndApplicationIdAndStage(100L, 1, Stage.L1))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> candidateService.advanceStage(100L));
    }

    /**
     * Tests that HR stage moves the candidate to SELECTED status.
     */
    @Test
    void testAdvanceHrToSelected() {
        existingCandidate.setCurrentStage(Stage.HR);

        Panel p = new Panel();
        p.setId(2L);
        Interview iv = new Interview();
        iv.setId(99L);
        iv.setPanels(List.of(p));

        Feedback fb = new Feedback();
        fb.setId(1L);

        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        when(interviewRepository.findByCandidateIdAndApplicationIdAndStage(100L, 1, Stage.HR))
                .thenReturn(Optional.of(iv));
        when(feedbackRepository.findByInterviewId(99L)).thenReturn(List.of(fb));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidateResponseDTO response = candidateService.advanceStage(100L);
        assertEquals(CandidateStatus.SELECTED, response.getStatus());
    }

    /**
     * Tests that a rejected candidate cannot advance further.
     */
    @Test
    void testAdvanceRejectedCandidate() {
        existingCandidate.setStatus(CandidateStatus.REJECTED);
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        assertThrows(RuntimeException.class, () -> candidateService.advanceStage(100L));
    }

    /**
     * Tests that a selected candidate cannot advance further.
     */
    @Test
    void testAdvanceSelectedCandidate() {
        existingCandidate.setStatus(CandidateStatus.SELECTED);
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        assertThrows(RuntimeException.class, () -> candidateService.advanceStage(100L));
    }

    /**
     * Tests advance stage when candidate is not found.
     */
    @Test
    void testAdvanceCandidateNotFound() {
        when(candidateRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> candidateService.advanceStage(100L));
    }

    /**
     * Tests rejecting a candidate successfully.
     */
    @Test
    void testRejectCandidateSuccess() {
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenAnswer(inv -> inv.getArgument(0));
        CandidateResponseDTO response = candidateService.rejectCandidate(100L);
        assertEquals(CandidateStatus.REJECTED, response.getStatus());
    }

    /**
     * Tests that rejecting an already rejected candidate throws an exception.
     */
    @Test
    void testRejectAlreadyRejected() {
        existingCandidate.setStatus(CandidateStatus.REJECTED);
        when(candidateRepository.findById(100L)).thenReturn(Optional.of(existingCandidate));
        assertThrows(RuntimeException.class, () -> candidateService.rejectCandidate(100L));
    }

    /**
     * Tests rejecting a candidate that does not exist.
     */
    @Test
    void testRejectCandidateNotFound() {
        when(candidateRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> candidateService.rejectCandidate(100L));
    }

    /**
     * Tests that fetching status by an unknown email returns null.
     */
    @Test
    void testGetStatusByEmailNotFound() {
        when(candidateRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());
        assertNull(candidateService.getApplicationStatusByEmail("ghost@example.com"));
    }

    /**
     * Tests application status for a candidate currently in progress at L1 stage.
     */
    @Test
    void testGetStatusByEmailInProgress() {
        existingCandidate.setStatus(CandidateStatus.IN_PROGRESS);
        existingCandidate.setCurrentStage(Stage.L1);
        existingCandidate.setResumeUrl("uploads/resumes/x.pdf");

        Panel p = new Panel();
        p.setId(2L);
        p.setName("Panel A");
        Interview iv = new Interview();
        iv.setId(99L);
        iv.setStage(Stage.L1);
        iv.setStatus(InterviewStatus.SCHEDULED);
        iv.setPanels(List.of(p));

        when(candidateRepository.findByEmail("Rahul@example.com")).thenReturn(Optional.of(existingCandidate));
        when(interviewRepository.findByCandidateIdWithPanels(100L)).thenReturn(List.of(iv));
        when(feedbackRepository.findByInterviewId(99L)).thenReturn(List.of());

        ApplicationStatusDTO dto = candidateService.getApplicationStatusByEmail("Rahul@example.com");
        assertNotNull(dto);
        assertEquals("L1", dto.getDerivedStatus());
        assertTrue(dto.isLocked());
        assertTrue(dto.isResumeUploaded());
    }

    /**
     * Tests that a rejected candidate has a derived status of "Rejected".
     */
    @Test
    void testGetStatusByEmailRejected() {
        existingCandidate.setStatus(CandidateStatus.REJECTED);
        when(candidateRepository.findByEmail("Rahul@example.com")).thenReturn(Optional.of(existingCandidate));
        when(interviewRepository.findByCandidateIdWithPanels(100L)).thenReturn(List.of());

        ApplicationStatusDTO dto = candidateService.getApplicationStatusByEmail("Rahul@example.com");
        assertEquals("Rejected", dto.getDerivedStatus());
    }

    /**
     * Tests that a selected candidate has a derived status of "Completed".
     */
    @Test
    void testGetStatusByEmailSelected() {
        existingCandidate.setStatus(CandidateStatus.SELECTED);
        when(candidateRepository.findByEmail("Rahul@example.com")).thenReturn(Optional.of(existingCandidate));
        when(interviewRepository.findByCandidateIdWithPanels(100L)).thenReturn(List.of());

        ApplicationStatusDTO dto = candidateService.getApplicationStatusByEmail("Rahul@example.com");
        assertEquals("Completed", dto.getDerivedStatus());
    }
}