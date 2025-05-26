package com.example.DeliveryTeamDashboard.Entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "job_descriptions")
@Data
public class JobDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String client;
    private LocalDate receivedDate;
    private LocalDate deadline;
    private String technology;
    private String resourceType;
    private String description;
    private String s3Key; // S3 key for the JD file
}
