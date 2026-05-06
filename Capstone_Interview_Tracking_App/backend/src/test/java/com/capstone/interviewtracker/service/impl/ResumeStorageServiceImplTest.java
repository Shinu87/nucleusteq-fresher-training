package com.capstone.interviewtracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Test class for ResumeStorageServiceImpl.
 */
class ResumeStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private ResumeStorageServiceImpl service;

    /**
     * Sets up the service with a temporary directory before each test.
     */
    @BeforeEach
    void setUp() {
        service = new ResumeStorageServiceImpl();
        // use temp directory so real files are not created
        ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());
        service.init();
    }

    /**
     * Tests storing a valid PDF resume.
     */
    @Test
    void testStoreResumeSuccess() {
        MultipartFile file = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", "PDF-content".getBytes());

        String stored = service.storeResume(file);

        assertNotNull(stored);
        assertTrue(stored.endsWith(".pdf"));
    }

    /**
     * Tests that an empty file is rejected.
     */
    @Test
    void testStoreResumeRejectsEmptyFile() {
        MultipartFile empty = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", new byte[0]);
        assertThrows(RuntimeException.class, () -> service.storeResume(empty));
    }

    /**
     * Tests that a non-PDF content type is rejected.
     */
    @Test
    void testStoreResumeRejectsNonPdfContentType() {
        MultipartFile txt = new MockMultipartFile(
                "file", "resume.txt", "text/plain", "hello".getBytes());
        assertThrows(RuntimeException.class, () -> service.storeResume(txt));
    }

    /**
     * Tests that a file with non-pdf extension is rejected.
     */
    @Test
    void testStoreResumeRejectsNonPdfExtension() {
        MultipartFile file = new MockMultipartFile(
                "file", "resume.exe", "application/pdf", "PDF-content".getBytes());
        assertThrows(RuntimeException.class, () -> service.storeResume(file));
    }

    /**
     * Tests that path traversal in the filename is rejected.
     */
    @Test
    void testStoreResumeRejectsPathTraversal() {
        MultipartFile file = new MockMultipartFile(
                "file", "../etc/passwd.pdf", "application/pdf", "PDF-content".getBytes());
        assertThrows(RuntimeException.class, () -> service.storeResume(file));
    }

    /**
     * Tests that deleting a null path does not throw an exception.
     */
    @Test
    void testDeleteResumeNullPath() {
        service.deleteResume(null);
    }

    /**
     * Tests that deleting a blank path does not throw an exception.
     */
    @Test
    void testDeleteResumeBlankPath() {
        service.deleteResume("   ");
    }

    /**
     * Tests that an existing file is deleted properly.
     */
    @Test
    void testDeleteResumeRemovesFile() throws IOException {
        Path file = tempDir.resolve("test.pdf");
        Files.write(file, "x".getBytes());

        service.deleteResume(file.toString());

        assertTrue(!Files.exists(file));
    }

    /**
     * Tests that deleting a missing file does not throw an exception.
     */
    @Test
    void testDeleteResumeMissingFile() {
        service.deleteResume(tempDir.resolve("does-not-exist.pdf").toString());
    }
}