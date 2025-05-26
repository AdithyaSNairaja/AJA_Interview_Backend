package com.example.DeliveryTeamDashboard.Entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "mock_interviews")
@Data
public class MockInterview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDate date;
    private String interviewer;
    private String status;
    private Integer technicalRating;
    private Integer communicationRating;
    private String technicalFeedback;
    private String communicationFeedback;
    private boolean sentToSales;
    private boolean deployed;
}
