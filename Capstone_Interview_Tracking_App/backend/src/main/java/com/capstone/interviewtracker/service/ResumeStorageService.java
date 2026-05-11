package com.capstone.interviewtracker.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service for handling resume file storage operations.
 * Provides methods to store and delete uploaded resumes.
 */
public interface ResumeStorageService {

    /**
     * Stores an uploaded resume file on the server or storage system.
     *
     * @param file resume file uploaded by the user
     * @return stored file path or URL of the uploaded resume
     */
    String storeResume(MultipartFile file);

    /**
     * Deletes a stored resume file from the storage system.
     *
     * @param filePath path or URL of the resume file to delete
     */
    void deleteResume(String filePath);
}