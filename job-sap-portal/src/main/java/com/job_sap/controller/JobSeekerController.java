// src/main/java/com/job_sap/controller/JobSeekerController.java
package com.job_sap.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.job_sap.entity.Application;
import com.job_sap.entity.Job;
import com.job_sap.entity.User;
import com.job_sap.repository.AppRepository;
import com.job_sap.repository.JobRepository;
import com.job_sap.repository.UserRepository;
import com.job_sap.service.JobService;
import com.job_sap.service.ApplicationService;

@RestController
@RequestMapping("/api/jobseeker")
public class JobSeekerController {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private AppRepository appRepository;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JobService jobService;
	@Autowired
	private ApplicationService applicationService;
	// Helper method
	private Long getLoggedInUserId(Principal principal) {
		User user = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		return user.getId();
	}

	// 1. View All Jobs
	@GetMapping("/jobs")
	public ResponseEntity<List<Job>> getAllJobs() {
		return ResponseEntity.ok(jobRepository.findAll());
	}

	// 2. Search Jobs
	@GetMapping("/jobs/search")
	public ResponseEntity<List<Job>> searchJobs(@RequestParam String keyword) {
		return ResponseEntity
				.ok(jobRepository.findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(keyword, keyword));
	}
	
	// View a single job's details by ID
		@GetMapping("/jobs/{jobId}")
		public ResponseEntity<Job> getJobById(@PathVariable Long jobId) {
			Job job = jobRepository.findById(jobId)
					.orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));
			return ResponseEntity.ok(job);
		}

	// 3. Apply for a Job (Includes Resume Link)
	@PreAuthorize("hasAnyAuthority('JOBSEEKER','ROLE_JOBSEEKER')") // ONLY Jobseekers allowed
	@PostMapping("/apply/{jobId}")
	public ResponseEntity<?> applyForJob(@PathVariable Long jobId, @RequestParam String resumeLink,
			Principal principal) {
		// Check if job exists
		jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
		
		Long candidateId = getLoggedInUserId(principal);
		if (appRepository.existsByJobIdAndCandidateId(jobId, candidateId)) {
			return ResponseEntity.badRequest().body("You have already applied for this job.");
		}
		
		Application app = new Application();
		app.setJobId(jobId);
		app.setCandidateId(getLoggedInUserId(principal));
		app.setResumeLink(resumeLink);
		return ResponseEntity.ok(
		        applicationService.applyJob(app)
		);
	}

	// 4. Track Application Status
	@PreAuthorize("hasAnyAuthority('JOBSEEKER','ROLE_JOBSEEKER')") // ONLY Jobseekers allowed
	@GetMapping("/applications")
	public ResponseEntity<List<Application>> getMyApplications(Principal principal) {
		return ResponseEntity.ok(appRepository.findByCandidateId(getLoggedInUserId(principal)));
	}

	// Filter By Location
	@GetMapping("/jobs/filter/location")
	public ResponseEntity<List<Job>> filterByLocation(@RequestParam String location) {

		return ResponseEntity.ok(jobService.filterByLocation(location));
	}

	// Filter By Skills
	@GetMapping("/jobs/filter/skills")
	public ResponseEntity<List<Job>> filterBySkills(@RequestParam String skills) {

		return ResponseEntity.ok(jobService.filterBySkills(skills));
	}

	// Filter By Experience
	@GetMapping("/jobs/filter/experience")
	public ResponseEntity<List<Job>> filterByExperience(@RequestParam Integer experience) {

		return ResponseEntity.ok(jobService.filterByExperience(experience));
	}

}