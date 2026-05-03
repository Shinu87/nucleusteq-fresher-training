package com.capstone.interviewtracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.interviewtracker.constants.api.CandidateApiConstants;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles APIs for downloading resume files.
 * Files are served from internal storage.
 */
@RestController
@RequestMapping(CandidateApiConstants.RESUMES_BASE_PATH)
public class ResumeController {

    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);

    /**
     * Downloads a resume file using its stored path.
     * Validates path to prevent unauthorized file access.
     *
     * @param path relative path of the resume file
     * @return resume file resource
     */
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
}