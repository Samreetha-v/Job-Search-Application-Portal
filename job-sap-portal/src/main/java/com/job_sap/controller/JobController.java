package com.job_sap.controller;

import com.job_sap.entity.Job;
import com.job_sap.repository.JobRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    // ONLY RECRUITERS CAN POST JOBS
    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        return ResponseEntity.ok(jobRepository.save(job));
    }

    // BOTH JOBSEEKER & RECRUITER CAN VIEW JOBS
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobRepository.findAll());
    }

    // VIEW SINGLE JOB
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        return ResponseEntity.ok(job);
    }

    // ONLY RECRUITER CAN VIEW THEIR POSTED JOBS
    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<List<Job>> getJobsByRecruiter(
            @PathVariable Long recruiterId) {

        return ResponseEntity.ok(
                jobRepository.findByRecruiterId(recruiterId)
        );
    }
}