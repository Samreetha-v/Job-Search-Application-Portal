// 7. controller/AppController.java
package com.job_sap.controller;

import com.job_sap.entity.Application;
import com.job_sap.repository.AppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class AppController {

    @Autowired
    private AppRepository appRepository;

    @PostMapping
    public ResponseEntity<Application> applyForJob(@RequestBody Application application) {
        return ResponseEntity.ok(appRepository.save(application));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsForJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(appRepository.findByJobId(jobId));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<Application>> getApplicationsByCandidate(@PathVariable Long candidateId) {
        return ResponseEntity.ok(appRepository.findByCandidateId(candidateId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Application> updateApplicationStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        Application application = appRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStatus(status);
        return ResponseEntity.ok(appRepository.save(application));
    }
}