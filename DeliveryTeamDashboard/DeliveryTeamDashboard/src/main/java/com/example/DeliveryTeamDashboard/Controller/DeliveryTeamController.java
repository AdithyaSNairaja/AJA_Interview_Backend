package com.example.DeliveryTeamDashboard.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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


@RestController
@RequestMapping("/api/delivery")
public class DeliveryTeamController {

    private final DeliveryTeamService deliveryTeamService;

    public DeliveryTeamController(DeliveryTeamService deliveryTeamService) {
        this.deliveryTeamService = deliveryTeamService;
    }

    @GetMapping("/employees")
    public List<Employee> getEmployees(
            @RequestParam(defaultValue = "all") String technology,
            @RequestParam(defaultValue = "all") String resourceType) {
        return deliveryTeamService.getEmployees(technology, resourceType);
    }

    @PostMapping("/mock-interviews")
    public MockInterview scheduleMockInterview(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String interviewer) {
        return deliveryTeamService.scheduleMockInterview(employeeId, date, interviewer);
    }

    @PutMapping("/mock-interviews/{interviewId}/feedback")
    public MockInterview updateMockInterviewFeedback(
            @PathVariable Long interviewId,
            @RequestParam Integer technicalRating,
            @RequestParam Integer communicationRating,
            @RequestParam String technicalFeedback,
            @RequestParam String communicationFeedback,
            @RequestParam boolean sentToSales) {
        return deliveryTeamService.updateMockInterviewFeedback(interviewId, technicalRating,
                communicationRating, technicalFeedback, communicationFeedback, sentToSales);
    }

    @GetMapping("/mock-interviews/upcoming")
    public List<MockInterview> getUpcomingInterviews() {
        return deliveryTeamService.getUpcomingInterviews();
    }

    @GetMapping("/mock-interviews/completed")
    public List<MockInterview> getCompletedInterviews() {
        return deliveryTeamService.getCompletedInterviews();
    }
}