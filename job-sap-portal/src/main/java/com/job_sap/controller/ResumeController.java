package com.job_sap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.job_sap.service.ResumeService;

@RestController
@RequestMapping("/api")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    // Upload Resume
    @PostMapping("/upload/resume")
    @PreAuthorize("hasAuthority('ROLE_JOBSEEKER')")
    public ResponseEntity<String> uploadResume(
            @RequestParam("file") MultipartFile file) {

        String uploadedFile =
                resumeService.uploadResume(file);

        return ResponseEntity.ok(
                "Resume uploaded successfully: "
                        + uploadedFile
        );
    }
}