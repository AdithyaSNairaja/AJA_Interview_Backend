package com.example.DeliveryTeamDashboard.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.DeliveryTeamDashboard.Entity.Employee;
import com.example.DeliveryTeamDashboard.Entity.MockInterview;
import com.example.DeliveryTeamDashboard.Repository.EmployeeRepository;
import com.example.DeliveryTeamDashboard.Repository.MockInterviewRepository;

@Service
public class DeliveryTeamService {

    private final EmployeeRepository employeeRepository;
    private final MockInterviewRepository mockInterviewRepository;

    public DeliveryTeamService(EmployeeRepository employeeRepository,
                               MockInterviewRepository mockInterviewRepository) {
        this.employeeRepository = employeeRepository;
        this.mockInterviewRepository = mockInterviewRepository;
    }

    public List<Employee> getEmployees(String technology, String resourceType) {
        if (!"all".equalsIgnoreCase(technology)) {
            return employeeRepository.findByTechnology(technology);
        }
        if (!"all".equalsIgnoreCase(resourceType)) {
            return employeeRepository.findByResourceType(resourceType);
        }
        return employeeRepository.findAll();
    }

    public MockInterview scheduleMockInterview(Long employeeId, LocalDate date, String interviewer) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        MockInterview interview = new MockInterview();
        interview.setEmployee(employee);
        interview.setDate(date);
        interview.setInterviewer(interviewer);
        interview.setStatus("scheduled");
        return mockInterviewRepository.save(interview);
    }

    public MockInterview updateMockInterviewFeedback(Long interviewId, Integer technicalRating,
                                                    Integer communicationRating, String technicalFeedback,
                                                    String communicationFeedback, boolean sentToSales) {
        MockInterview interview = mockInterviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));
        interview.setTechnicalRating(technicalRating);
        interview.setCommunicationRating(communicationRating);
        interview.setTechnicalFeedback(technicalFeedback);
        interview.setCommunicationFeedback(communicationFeedback);
        interview.setSentToSales(sentToSales);
        interview.setStatus("completed");
        return mockInterviewRepository.save(interview);
    }

    public List<MockInterview> getUpcomingInterviews() {
        return mockInterviewRepository.findByStatus("scheduled");
    }

    public List<MockInterview> getCompletedInterviews() {
        return mockInterviewRepository.findByStatus("completed");
    }
}