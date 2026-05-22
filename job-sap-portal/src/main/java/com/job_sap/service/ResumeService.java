package com.job_sap.service;

import java.io.IOException;
import java.nio.file.*;

// NEW IMPORTS
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeService {

    private final String uploadDir = "uploads/resumes/";

    // Upload Resume (Unchanged)
    public String uploadResume(MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Resume upload failed");
        }
    }

    // Download Resume (CHANGED: Now returns a Resource)
    public Resource downloadResume(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir)
                    .resolve(fileName)
                    .normalize();

            if (!Files.exists(filePath)) {
                throw new RuntimeException("Resume not found");
            }

            // FIX: Convert the Path into a UrlResource so the Controller can download it!
            return new UrlResource(filePath.toUri());

        } catch (Exception e) {
            throw new RuntimeException("Resume download failed");
        }
    }
}