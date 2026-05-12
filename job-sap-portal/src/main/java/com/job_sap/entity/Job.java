// 1. entity/Job.java
package com.job_sap.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private String company;
    private String location;
    
    private Long recruiterId;
    
    private LocalDateTime postedAt = LocalDateTime.now();
}