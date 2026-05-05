package com.capstone.interviewtracker.service.impl;

import com.capstone.interviewtracker.constants.messages.ValidationMessages;
import com.capstone.interviewtracker.service.ResumeStorageService;

import jakarta.annotation.PostConstruct;

import com.capstone.interviewtracker.exception.custom.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Stores resume files on the local server filesystem under /uploads/resumes.
 * Only PDF files are accepted.
 */
@Service
public class ResumeStorageServiceImpl implements ResumeStorageService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeStorageServiceImpl.class);
    private static final String ALLOWED_CONTENT_TYPE = "application/pdf";
    private static final String PDF_EXTENSION = ".pdf";

    @Value("${resume.upload.dir:uploads/resumes}")
    private String uploadDir;

    private Path storagePath;

    /**
     * Initializes the storage directory on application startup.
     */
    @PostConstruct
    public void init() {
        storagePath = Paths.get(uploadDir).toAbsolutePath().normalize();

        logger.debug("Attempting to create resume storage directory at: {}", storagePath);

        try {
            Files.createDirectories(storagePath);
            logger.info("Resume storage directory initialized at: {}", storagePath);
        } catch (IOException e) {
            logger.error("Failed to create resume storage directory at: {} - Error: {}", storagePath, e.getMessage());
            throw new BadRequestException(
                    ValidationMessages.RESUME_DIRECTORY_CREATION_FAILED + ": " + storagePath);
        }
    }

    /**
     * Stores resume file with a UUID-based unique filename.
     * Validates that only PDF files are uploaded.
     *
     * @param file the uploaded file (must be PDF)
     * @return relative file path stored in database
     */
    @Override
    public String storeResume(MultipartFile file) {

        logger.info("Resume upload request received");

        if (file == null || file.isEmpty()) {
            logger.warn("Resume upload failed - file is null or empty");
            throw new BadRequestException(
                    ValidationMessages.RESUME_FILE_REQUIRED);
        }

        logger.debug("Validating content type for uploaded file: {}", file.getOriginalFilename());

        /* Validate PDF content type */
        String contentType = file.getContentType();
        if (!ALLOWED_CONTENT_TYPE.equals(contentType)) {
            logger.warn("Resume upload failed - invalid content type: {}", contentType);
            throw new BadRequestException(
                    ValidationMessages.INVALID_RESUME_FILE_TYPE + ". Received: " + contentType);
        }

        logger.debug("Validating file extension for: {}", file.getOriginalFilename());

        /* Validate file extension */
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "resume");
        if (!originalFilename.toLowerCase().endsWith(PDF_EXTENSION)) {
            logger.warn("Resume upload failed - invalid file extension for filename: {}", originalFilename);
            throw new BadRequestException(
                    ValidationMessages.INVALID_RESUME_EXTENSION);
        }

        logger.debug("Checking for path traversal in filename: {}", originalFilename);

        /* Check for path traversal attack */
        if (originalFilename.contains("..")) {
            logger.warn("Resume upload failed - path traversal detected in filename: {}", originalFilename);
            throw new BadRequestException(
                    ValidationMessages.INVALID_FILENAME_DETECTED);
        }

        /* Generate unique filename: UUID + original name (trimmed) */
        String uniqueFilename = UUID.randomUUID().toString() + "_" +
                originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        logger.debug("Generated unique filename for resume: {}", uniqueFilename);

        try {
            Path targetPath = storagePath.resolve(uniqueFilename);

            logger.debug("Copying resume file to target path: {}", targetPath);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Resume stored at: {}", targetPath);

            logger.debug("Returning relative path for DB storage: {}/{}", uploadDir, uniqueFilename);

            return uploadDir + "/" + uniqueFilename;
        } catch (IOException e) {
            logger.error("Resume upload failed while copying file: {} - Error: {}", uniqueFilename, e.getMessage());
            throw new BadRequestException(
                    ValidationMessages.RESUME_UPLOAD_FAILED + ": " + e.getMessage());
        }
    }

    /**
     * Deletes a stored resume file from the filesystem if it exists.
     *
     * @param filePath absolute or relative path of the resume file
     */
    @Override
    public void deleteResume(String filePath) {

        logger.info("Delete resume request received for path: {}", filePath);

        if (filePath == null || filePath.isBlank()) {
            logger.warn("Delete resume skipped - file path is null or blank");
            return;
        }

        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();

            logger.debug("Attempting to delete resume file at absolute path: {}", path);

            Files.deleteIfExists(path);
            logger.info("Resume deleted: {}", path);
        } catch (IOException e) {
            logger.warn("Could not delete resume file at {}: {}", filePath, e.getMessage());
        }
    }
}