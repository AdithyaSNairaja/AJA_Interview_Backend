package com.example.DeliveryTeamDashboard.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DeliveryTeamDashboard.Entity.Employee;
import com.example.DeliveryTeamDashboard.Entity.MockInterview;
import com.example.DeliveryTeamDashboard.Service.DeliveryTeamService;
import com.example.DeliveryTeamDashboard.Service.EmployeeService;


@RestController
@RequestMapping("/api/delivery")
public class DeliveryTeamController {
    
    private final DeliveryTeamService deliveryTeamService;
    private final EmployeeService employeeService;

    public DeliveryTeamController(DeliveryTeamService deliveryTeamService, EmployeeService employeeService) {
        this.deliveryTeamService = deliveryTeamService;
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public List<Employee> getEmployees(
            @RequestParam(defaultValue = "all") String technology,
            @RequestParam(defaultValue = "all") String resourceType) {
        return deliveryTeamService.getEmployees(technology, resourceType);
    }

       @PostMapping("/schedule")
    @PreAuthorize("hasRole('DELIVERY_TEAM')")
    public ResponseEntity<?> scheduleInterview(
            Authentication authentication,
            @RequestParam String empId,
            @RequestParam String interviewType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime time,
            @RequestParam(required = false) String client,
            @RequestParam(required = false) Long interviewerId, // Changed to Long
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String jobDescriptionTitle,
            @RequestParam(required = false) String meetingLink) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (!"mock".equalsIgnoreCase(interviewType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Delivery team can only schedule mock interviews");
        }
        try {
            MockInterview interview = deliveryTeamService.scheduleMockInterview(empId, date, time, interviewerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(interview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PutMapping("/mock-interviews/{interviewId}/feedback")
    @PreAuthorize("hasRole('DELIVERY_TEAM')")
    public ResponseEntity<?> updateMockInterviewFeedback(
            Authentication authentication,
            @PathVariable Long interviewId,
            @RequestParam String feedback,
            @RequestParam Integer technicalScore,
            @RequestParam Integer communicationScore) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        try {
            MockInterview interview = deliveryTeamService.updateMockInterviewFeedback(interviewId, feedback, technicalScore, communicationScore);
            return ResponseEntity.ok(interview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/interviews/upcoming")
    public List<MockInterview> getUpcomingInterviews() {
        return deliveryTeamService.getUpcomingInterviews();
    }

    @GetMapping("/interviews/completed")
    public List<MockInterview> getCompletedInterviews() {
        return deliveryTeamService.getCompletedInterviews();
    }
}