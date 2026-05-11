package com.capstone.interviewtracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.capstone.interviewtracker.dto.Request.JobRequestDTO;
import com.capstone.interviewtracker.dto.Response.JobResponseDTO;
import com.capstone.interviewtracker.enums.JobType;
import com.capstone.interviewtracker.exception.custom.ResourceNotFoundException;
import com.capstone.interviewtracker.model.JobDescription;
import com.capstone.interviewtracker.model.Skill;
import com.capstone.interviewtracker.repository.JobDescriptionRepository;
import com.capstone.interviewtracker.repository.SkillRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for JobServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock
    private JobDescriptionRepository jobRepo;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    private JobRequestDTO request;
    private Skill skill;
    private JobDescription saved;

    /**
     * Sets up common test data before each test.
     */
    @BeforeEach
    void setUp() {
        skill = new Skill("Java");
        try {
            var f = Skill.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(skill, 1L);
        } catch (Exception ignored) {
        }

        request = new JobRequestDTO();
        request.setTitle("Backend Engineer");
        request.setDescription("Build REST APIs");
        request.setSkillIds(List.of(1L));
        request.setMinExperience(2);
        request.setMaxExperience(5);
        request.setMinSalary(500000.0);
        request.setMaxSalary(1500000.0);
        request.setLocation("Pune");
        request.setJobType(JobType.FULL_TIME);

        saved = new JobDescription();
        try {
            var f = JobDescription.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(saved, 100L);
        } catch (Exception ignored) {
        }
        saved.setTitle("Backend Engineer");
        saved.setDescription("Build REST APIs");
        saved.setMinExperience(2);
        saved.setMaxExperience(5);
        saved.setMinSalary(500000.0);
        saved.setMaxSalary(1500000.0);
        saved.setLocation("Pune");
        saved.setJobType(JobType.FULL_TIME);
        saved.setActive(true);
        saved.setSkills(List.of(skill));
    }

    /**
     * Tests creating a job with valid data.
     */
    @Test
    void testCreateJobSuccess() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(jobRepo.save(any(JobDescription.class))).thenReturn(saved);

        JobResponseDTO result = jobService.createJob(request);

        assertEquals("Backend Engineer", result.getTitle());
        assertTrue(result.isActive());
        assertEquals(List.of("Java"), result.getSkills());
    }

    /**
     * Tests that negative min experience is not allowed.
     */
    @Test
    void testCreateJobNegativeMinExperience() {
        request.setMinExperience(-1);
        assertThrows(RuntimeException.class, () -> jobService.createJob(request));
    }

    /**
     * Tests that negative max experience is not allowed.
     */
    @Test
    void testCreateJobNegativeMaxExperience() {
        request.setMaxExperience(-3);
        assertThrows(RuntimeException.class, () -> jobService.createJob(request));
    }

    /**
     * Tests that min experience cannot be greater than max experience.
     */
    @Test
    void testCreateJobMinGreaterThanMaxExperience() {
        request.setMinExperience(7);
        request.setMaxExperience(3);
        assertThrows(RuntimeException.class, () -> jobService.createJob(request));
    }

    /**
     * Tests that negative min salary is not allowed.
     */
    @Test
    void testCreateJobNegativeMinSalary() {
        request.setMinSalary(-100.0);
        assertThrows(RuntimeException.class, () -> jobService.createJob(request));
    }

    /**
     * Tests that negative max salary is not allowed.
     */
    @Test
    void testCreateJobNegativeMaxSalary() {
        request.setMaxSalary(-100.0);
        assertThrows(RuntimeException.class, () -> jobService.createJob(request));
    }

    /**
     * Tests that min salary cannot be greater than max salary.
     */
    @Test
    void testCreateJobMinGreaterThanMaxSalary() {
        request.setMinSalary(2000000.0);
        request.setMaxSalary(1000000.0);
        assertThrows(RuntimeException.class, () -> jobService.createJob(request));
    }

    /**
     * Tests that creating a job with an empty skill list fails.
     */
    @Test
    void testCreateJobEmptySkillIds() {
        request.setSkillIds(List.of());
        assertThrows(RuntimeException.class, () -> jobService.createJob(request));
    }

    /**
     * Tests that creating a job with a null skill list fails.
     */
    @Test
    void testCreateJobNullSkillIds() {
        request.setSkillIds(null);
        assertThrows(RuntimeException.class, () -> jobService.createJob(request));
    }

    /**
     * Tests that creating a job fails when a skill is not found.
     */
    @Test
    void testCreateJobSkillNotFound() {
        when(skillRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> jobService.createJob(request));
    }

    /**
     * Tests fetching all jobs returns the mapped list.
     */
    @Test
    void testGetAllJobs() {
        when(jobRepo.findAllWithSkills()).thenReturn(List.of(saved));
        List<JobResponseDTO> result = jobService.getAllJobs();
        assertEquals(1, result.size());
        assertEquals("Backend Engineer", result.get(0).getTitle());
    }

    /**
     * Tests fetching only active jobs.
     */
    @Test
    void testGetActiveJobs() {
        when(jobRepo.findAllActiveWithSkills()).thenReturn(List.of(saved));
        List<JobResponseDTO> result = jobService.getActiveJobs();
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    /**
     * Tests deactivating an active job.
     */
    @Test
    void testDeactivateJobSuccess() {
        when(jobRepo.findById(100L)).thenReturn(Optional.of(saved));
        when(jobRepo.save(any(JobDescription.class))).thenAnswer(inv -> inv.getArgument(0));

        JobResponseDTO result = jobService.deactivateJob(100L);

        assertFalse(result.isActive());
    }

    /**
     * Tests that deactivating an already inactive job throws an exception.
     */
    @Test
    void testDeactivateJobAlreadyInactive() {
        saved.setActive(false);
        when(jobRepo.findById(100L)).thenReturn(Optional.of(saved));
        assertThrows(RuntimeException.class, () -> jobService.deactivateJob(100L));
    }

    /**
     * Tests deactivating a job that does not exist.
     */
    @Test
    void testDeactivateJobNotFound() {
        when(jobRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> jobService.deactivateJob(999L));
    }

    /**
     * Tests activating an inactive job successfully.
     */
    @Test
    void testActivateJobSuccess() {
        saved.setActive(false);
        when(jobRepo.findById(100L)).thenReturn(Optional.of(saved));
        when(jobRepo.save(any(JobDescription.class))).thenAnswer(inv -> inv.getArgument(0));

        JobResponseDTO result = jobService.activateJob(100L);

        assertTrue(result.isActive());
    }

    /**
     * Tests that activating an already active job throws an exception.
     */
    @Test
    void testActivateJobAlreadyActive() {
        when(jobRepo.findById(100L)).thenReturn(Optional.of(saved));
        assertThrows(RuntimeException.class, () -> jobService.activateJob(100L));
    }

    /**
     * Tests activating a job that does not exist.
     */
    @Test
    void testActivateJobNotFound() {
        when(jobRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> jobService.activateJob(999L));
    }
}
