package com.job_sap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.job_sap.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

    // Recruiter jobs
    List<Job> findByRecruiterId(Long recruiterId);

    // Search jobs
    List<Job> findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(
            String title,
            String company
    );

    // Service layer methods
    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findBySkillsContainingIgnoreCase(String skills);

    List<Job> findByExperienceLessThanEqual(Integer experience);
}