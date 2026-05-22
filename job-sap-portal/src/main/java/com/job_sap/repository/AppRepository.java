// 4. repository/AppRepository.java
package com.job_sap.repository;

import com.job_sap.entity.Application;
import jakarta.transaction.Transactional; // <-- Add this import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppRepository extends JpaRepository<Application, Long> {
	@Query("SELECT a FROM Application a JOIN FETCH a.candidate WHERE a.jobId = :jobId")
	List<Application> findByJobId(@Param("jobId") Long jobId);
    List<Application> findByCandidateId(Long candidateId);
 // NEW: Check if a user has already applied for a specific job
    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);
    @Transactional
    void deleteByJobId(Long jobId);
}