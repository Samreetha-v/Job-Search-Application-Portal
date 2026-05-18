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
@PreAuthorize("hasAuthority('ROLE_JOBSEEKER')") // ONLY Jobseekers allowed
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

	// 3. Apply for a Job (Includes Resume Link)
	@PostMapping("/apply/{jobId}")
	public ResponseEntity<Application> applyForJob(@PathVariable Long jobId, @RequestParam String resumeLink,
			Principal principal) {
		// Check if job exists
		jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

		Application app = new Application();
		app.setJobId(jobId);
		app.setCandidateId(getLoggedInUserId(principal));
		app.setResumeLink(resumeLink);
		return ResponseEntity.ok(
		        applicationService.applyJob(app)
		);
	}

	// 4. Track Application Status
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