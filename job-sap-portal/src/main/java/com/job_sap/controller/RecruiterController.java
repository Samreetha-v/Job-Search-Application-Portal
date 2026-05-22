// src/main/java/com/job_sap/controller/RecruiterController.java
package com.job_sap.controller;

import java.security.Principal;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import com.job_sap.entity.Application;
import com.job_sap.entity.Job;
import com.job_sap.entity.User;
import com.job_sap.enums.ApplicationStatus;
import com.job_sap.repository.AppRepository;
import com.job_sap.repository.JobRepository;
import com.job_sap.repository.UserRepository;
import com.job_sap.service.JobService;
import com.job_sap.service.ResumeService;

@RestController
@RequestMapping("/api/recruiter")
@PreAuthorize("hasAnyAuthority('RECRUITER','ROLE_RECRUITER')") // ONLY Recruiters allowed
public class RecruiterController {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private AppRepository appRepository;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ResumeService resumeService;
	@Autowired
	private JobService jobService;

	// Helper method to get the logged-in recruiter's ID
	private Long getLoggedInUserId(Principal principal) {
		User user = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		return user.getId();
	}

	// 1. Post Job
	@PostMapping("/jobs")
	public ResponseEntity<Job> postJob(@RequestBody Job job, Principal principal) {
		job.setRecruiterId(getLoggedInUserId(principal));
		return ResponseEntity.ok(jobRepository.save(job));
	}

	// 2. View Posted Jobs
	@GetMapping("/jobs")
	public ResponseEntity<List<Job>> getMyJobs(Principal principal) {
		return ResponseEntity.ok(jobRepository.findByRecruiterId(getLoggedInUserId(principal)));
	}

	// 3. Update Job
	@PutMapping("/jobs/{jobId}")
	public ResponseEntity<Job> updateJob(@PathVariable Long jobId, @RequestBody Job updatedJob, Principal principal) {
		Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

		// Security check: Ensure this recruiter owns this job
		if (!job.getRecruiterId().equals(getLoggedInUserId(principal))) {
			return ResponseEntity.status(403).build();
		}

		job.setTitle(updatedJob.getTitle());
		job.setDescription(updatedJob.getDescription());
		job.setCompany(updatedJob.getCompany());
		job.setLocation(updatedJob.getLocation());
		return ResponseEntity.ok(jobService.postJob(job));
	}

	// 4. Delete Job
	@DeleteMapping("/jobs/{jobId}")
	public ResponseEntity<String> deleteJob(@PathVariable Long jobId, Principal principal) {
		Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
		if (!job.getRecruiterId().equals(getLoggedInUserId(principal))) {
			return ResponseEntity.status(403).body("Not authorized to delete this job");
		}
		appRepository.deleteByJobId(jobId);
		jobRepository.delete(job);
		return ResponseEntity.ok("Job deleted successfully");
	}

	// 5. View Applicants for a Job
	@GetMapping("/jobs/{jobId}/applicants")
	public ResponseEntity<List<Application>> getApplicants(@PathVariable Long jobId, Principal principal) {
		Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
		if (!job.getRecruiterId().equals(getLoggedInUserId(principal))) {
			return ResponseEntity.status(403).build();
		}
		return ResponseEntity.ok(appRepository.findByJobId(jobId));
	}

	// 6. Shortlist / Update Status
	@PutMapping("/applications/{appId}/status")
	public ResponseEntity<?> updateApplicationStatus(@PathVariable Long appId, @RequestParam String status) {

		Application app = appRepository.findById(appId)
				.orElseThrow(() -> new RuntimeException("Application not found"));

		ApplicationStatus appStatus = ApplicationStatus.valueOf(status.trim().toUpperCase());

		app.setStatus(appStatus);

		return ResponseEntity.ok("Status updated successfully");
	}

	// 7. Download Resume
    @GetMapping("/download/resume/{fileName:.+}")
    public ResponseEntity<Resource> downloadResume(@PathVariable String fileName) {
        try {
            // The service now directly hands us the Resource!
            Resource resource = resumeService.downloadResume(fileName);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\""
                    )
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}