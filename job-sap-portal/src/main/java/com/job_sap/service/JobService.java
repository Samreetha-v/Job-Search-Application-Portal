package com.job_sap.service;

import com.job_sap.entity.Job;
import com.job_sap.repository.JobRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    // Post Job
    public Job postJob(Job job) {

        job.setPostedDate(LocalDate.now());

        return jobRepository.save(job);
    }

    // View All Jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Search Jobs
    public List<Job> searchJobs(String keyword) {
        return jobRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // Filter By Location
    public List<Job> filterByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }

    // Filter By Skills
    public List<Job> filterBySkills(String skills) {
        return jobRepository.findBySkillsContainingIgnoreCase(skills);
    }

    // Filter By Experience
    public List<Job> filterByExperience(Integer experience) {
        return jobRepository.findByExperienceLessThanEqual(experience);
    }

    // Delete Job
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}