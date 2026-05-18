package com.job_sap.service;

//import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.job_sap.entity.Application;
import com.job_sap.enums.ApplicationStatus;
import com.job_sap.repository.AppRepository;

@Service
public class ApplicationService {

    @Autowired
    private AppRepository appRepository;

    // Apply Job
    public Application applyJob(Application app) {

        app.setStatus(ApplicationStatus.APPLIED);

        app.setAppliedAt(java.time.LocalDateTime.now());

        return appRepository.save(app);
    }

    // Update Status
    public Application updateStatus(
            Long appId,
            ApplicationStatus status) {

        Application app = appRepository.findById(appId)
                .orElseThrow(() ->
                        new RuntimeException("Application not found"));

        app.setStatus(status);

        return appRepository.save(app);
    }

    // Get Candidate Applications
    public List<Application> getApplications(Long candidateId) {

        return appRepository.findByCandidateId(candidateId);
    }
}