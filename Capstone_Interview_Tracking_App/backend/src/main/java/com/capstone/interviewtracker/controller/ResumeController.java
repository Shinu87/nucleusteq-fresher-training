package com.capstone.interviewtracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.interviewtracker.constants.api.CandidateApiConstants;
import com.capstone.interviewtracker.model.Candidate;
import com.capstone.interviewtracker.repository.CandidateRepository;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles APIs for downloading resume files.
 * Files are served from internal storage.
 *
 * HR and PANEL roles can download any resume.
 * CANDIDATE role can only download their OWN resume.
 */
@RestController
@RequestMapping(CandidateApiConstants.RESUMES_BASE_PATH)
public class ResumeController {

    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);

    private final CandidateRepository candidateRepository;

    public ResumeController(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    /**
     * Downloads a resume file using its stored path.
     * Validates path to prevent unauthorized file access.
     * Candidate role is restricted to their own resume only.
     *
     * @param path relative path of the resume file
     * @return resume file resource
     */
    @PreAuthorize("hasAnyRole('HR','PANEL','CANDIDATE')")
    @GetMapping(CandidateApiConstants.RESUMES_DOWNLOAD)
    public ResponseEntity<Resource> downloadResume(
            @RequestParam String path) {

        try {
            Path filePath = Paths.get(path).normalize();

            String normalizedStr = filePath.toString().replace("\\", "/");

            if (!normalizedStr.startsWith("uploads/resumes/")) {
                logger.warn(
                        "Blocked suspicious resume download attempt for path: {}",
                        path);
                return ResponseEntity.badRequest().build();
            }

            /* If caller is CANDIDATE, allow only their OWN resume */
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && isCandidate(auth)) {
                String email = auth.getName();
                Candidate candidate = candidateRepository
                        .findByEmail(email)
                        .orElse(null);

                if (candidate == null
                        || candidate.getResumeUrl() == null
                        || !normalizedStr.equals(
                                candidate.getResumeUrl().replace("\\", "/"))) {
                    logger.warn(
                            "Blocked unauthorized resume access by candidate: {} for path: {}",
                            email, path);
                    return ResponseEntity.status(403).build();
                }
            }

            Path absolutePath = filePath.toAbsolutePath();
            Resource resource = new UrlResource(absolutePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                logger.warn(
                        "Resume file not found or not readable: {}",
                        absolutePath);
                return ResponseEntity.notFound().build();
            }

            String filename = absolutePath.getFileName().toString();

            String displayName = filename.contains("_")
                    ? filename.substring(filename.indexOf('_') + 1)
                    : filename;

            logger.info("Serving resume file: {}", absolutePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + displayName + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            logger.error("Invalid resume path: {}", path);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Checks if the authenticated user has the CANDIDATE role.
     *
     * @param auth current authentication
     * @return true if the user is a candidate
     */
    private boolean isCandidate(Authentication auth) {
        for (GrantedAuthority a : auth.getAuthorities()) {
            if ("ROLE_CANDIDATE".equals(a.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
