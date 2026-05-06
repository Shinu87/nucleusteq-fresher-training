package com.capstone.interviewtracker.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Properties;

import com.capstone.interviewtracker.enums.JobType;
import com.capstone.interviewtracker.enums.Stage;
import com.capstone.interviewtracker.model.Candidate;
import com.capstone.interviewtracker.model.Interview;
import com.capstone.interviewtracker.model.JobDescription;
import com.capstone.interviewtracker.model.Panel;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test class for EmailServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private Interview interview;
    private Panel panel;
    private MimeMessage mimeMessage;

    /**
     * Sets up common test data before each test.
     */
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@example.com");

        Session session = Session.getInstance(new Properties());
        mimeMessage = new MimeMessage(session);

        JobDescription job = new JobDescription();
        job.setTitle("SDE");
        job.setJobType(JobType.FULL_TIME);

        Candidate candidate = new Candidate();
        candidate.setName("Rahul");
        candidate.setEmail("rahul@example.com");
        candidate.setJobDescription(job);

        panel = new Panel();
        panel.setId(1L);
        panel.setName("Panel A");
        panel.setEmail("panel@example.com");

        interview = new Interview();
        interview.setId(100L);
        interview.setStage(Stage.L1);
        interview.setScheduledAt(LocalDateTime.of(2026, 6, 1, 10, 30));
        interview.setFocusArea("Java");
        interview.setMeetingUrl("https://meet.example.com/abc");
        interview.setCandidate(candidate);
    }

    /**
     * Tests that interview assignment email is sent successfully.
     */
    @Test
    void testSendInterviewAssignmentEmail() {
        emailService.sendInterviewAssignmentEmail(panel, interview);
        verify(mailSender, atLeastOnce()).send(any(SimpleMailMessage.class));
    }

    /**
     * Tests that interview email works even when meeting URL is null.
     */
    @Test
    void testSendInterviewAssignmentEmailNullMeetingUrl() {
        interview.setMeetingUrl(null);
        emailService.sendInterviewAssignmentEmail(panel, interview);
        verify(mailSender, atLeastOnce()).send(any(SimpleMailMessage.class));
    }

    /**
     * Tests that interview email works when focus area is null.
     */
    @Test
    void testSendInterviewAssignmentEmailNullFocusArea() {
        interview.setFocusArea(null);
        emailService.sendInterviewAssignmentEmail(panel, interview);
        verify(mailSender, atLeastOnce()).send(any(SimpleMailMessage.class));
    }

    /**
     * Tests that exceptions during sending are handled silently.
     */
    @Test
    void testSendInterviewAssignmentEmailHandlesException() {
        doThrow(new RuntimeException("smtp down")).when(mailSender).send(any(SimpleMailMessage.class));
        emailService.sendInterviewAssignmentEmail(panel, interview);
    }

    /**
     * Tests that panel activation email is sent successfully.
     */
    @Test
    void testSendPanelActivationEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailService.sendPanelActivationEmail("panel@example.com", "Panel A", "http://link");
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Tests that exceptions in panel activation email are handled.
     */
    @Test
    void testSendPanelActivationEmailHandlesException() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("smtp down")).when(mailSender).send(any(MimeMessage.class));
        emailService.sendPanelActivationEmail("panel@example.com", "Panel A", "http://link");
    }

    /**
     * Tests that candidate signup email is sent successfully.
     */
    @Test
    void testSendCandidateSignupEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailService.sendCandidateSignupEmail("c@example.com", "Priya", "http://link");
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Tests that candidate onboarding email is sent successfully.
     */
    @Test
    void testSendCandidateOnboardingEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailService.sendCandidateOnboardingEmail("c@example.com", "Priya", "http://link");
        verify(mailSender).send(any(MimeMessage.class));
    }

    /**
     * Tests that no email is sent when MimeMessage creation fails.
     */
    @Test
    void testSendCandidateOnboardingEmailCreationFails() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("oops"));
        emailService.sendCandidateOnboardingEmail("c@example.com", "Priya", "http://link");
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}