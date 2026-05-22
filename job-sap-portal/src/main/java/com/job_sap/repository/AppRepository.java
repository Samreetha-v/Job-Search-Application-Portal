// 4. repository/AppRepository.java
package com.job_sap.repository;

import com.job_sap.entity.Application;
import jakarta.transaction.Transactional; // <-- Add this import
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobId(Long jobId);
    List<Application> findByCandidateId(Long candidateId);
 // NEW: Check if a user has already applied for a specific job
    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);
    @Transactional
    void deleteByJobId(Long jobId);
}