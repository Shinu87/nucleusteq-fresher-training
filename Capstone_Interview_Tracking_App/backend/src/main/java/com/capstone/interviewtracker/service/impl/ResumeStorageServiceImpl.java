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
        try {
            Files.createDirectories(storagePath);
            logger.info("Resume storage directory initialized at: {}", storagePath);
        } catch (IOException e) {
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
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(
                    ValidationMessages.RESUME_FILE_REQUIRED);
        }

        /* Validate PDF content type */
        String contentType = file.getContentType();
        if (!ALLOWED_CONTENT_TYPE.equals(contentType)) {
            throw new BadRequestException(
                    ValidationMessages.INVALID_RESUME_FILE_TYPE + ". Received: " + contentType);
        }

        /* Validate file extension */
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "resume");
        if (!originalFilename.toLowerCase().endsWith(PDF_EXTENSION)) {
            throw new BadRequestException(
                    ValidationMessages.INVALID_RESUME_EXTENSION);
        }

        /* Check for path traversal attack */
        if (originalFilename.contains("..")) {
            throw new BadRequestException(
                    ValidationMessages.INVALID_FILENAME_DETECTED);
        }

        /* Generate unique filename: UUID + original name (trimmed) */
        String uniqueFilename = UUID.randomUUID().toString() + "_" +
                originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        try {
            Path targetPath = storagePath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Resume stored at: {}", targetPath);
            // Return relative path for DB storage
            return uploadDir + "/" + uniqueFilename;
        } catch (IOException e) {
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
        if (filePath == null || filePath.isBlank()) {
            return;
        }

        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            Files.deleteIfExists(path);
            logger.info("Resume deleted: {}", path);
        } catch (IOException e) {
            logger.warn("Could not delete resume file at {}: {}", filePath, e.getMessage());
        }
    }
}
