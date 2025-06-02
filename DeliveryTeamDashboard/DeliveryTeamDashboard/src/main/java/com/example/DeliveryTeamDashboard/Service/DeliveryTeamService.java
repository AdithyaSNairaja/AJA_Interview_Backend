package com.example.DeliveryTeamDashboard.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.DeliveryTeamDashboard.Entity.Employee;
import com.example.DeliveryTeamDashboard.Entity.MockInterview;
import com.example.DeliveryTeamDashboard.Entity.User;
import com.example.DeliveryTeamDashboard.Repository.EmployeeRepository;
import com.example.DeliveryTeamDashboard.Repository.MockInterviewRepository;
import com.example.DeliveryTeamDashboard.Repository.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class DeliveryTeamService {
    
	private final EmployeeRepository employeeRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final EmployeeService employeeService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public DeliveryTeamService(EmployeeRepository employeeRepository, 
                             MockInterviewRepository mockInterviewRepository, 
                             EmployeeService employeeService,
                             UserRepository userRepository,
                             EmailService emailService) {
        this.employeeRepository = employeeRepository;
        this.mockInterviewRepository = mockInterviewRepository;
        this.employeeService = employeeService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    
    public List<Employee> getEmployees(String technology, String resourceType) {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .filter(e -> "all".equalsIgnoreCase(technology) || e.getTechnology().equalsIgnoreCase(technology))
                .filter(e -> "all".equalsIgnoreCase(resourceType) || e.getResourceType().equalsIgnoreCase(resourceType))
                .collect(Collectors.toList());
    }

    public MockInterview scheduleMockInterview(String empId, LocalDate date, LocalTime time, Long interviewerId) {
    	MockInterview interview =(MockInterview) employeeService.scheduleInterview(empId, "mock", date, time, null, interviewerId, null, null, null);
    	  try {
              Employee employee = employeeRepository.findByEmpId(empId)
                      .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + empId));
              User interviewer = userRepository.findById(interviewerId)
                      .orElseThrow(() -> new IllegalArgumentException("Interviewer not found with ID: " + interviewerId));
              
              emailService.sendMockInterviewNotification(
                  employee.getUser(). getEmail(),
                  interviewer.getEmail(),
                  employee.getUser().getFullName(),
                  interviewer.getFullName(),
                  date,
                  time
              );
          } catch (MessagingException e) {
              // Log the error but don't fail the scheduling
              System.err.println("Failed to send email notifications: " + e.getMessage());
          }
          
          return interview;
        
        
    }

    public MockInterview updateMockInterviewFeedback(Long interviewId, String feedback, Integer technicalScore, Integer communicationScore) {
        MockInterview interview = mockInterviewRepository.findById(interviewId)
                .orElseThrow(() -> new IllegalArgumentException("Interview not found with ID: " + interviewId));
        interview.setTechnicalFeedback(feedback);
        interview.setTechnicalRating(technicalScore);
        interview.setCommunicationRating(communicationScore);
        interview.setStatus("completed");
        return mockInterviewRepository.save(interview);
    }

    public List<MockInterview> getUpcomingInterviews() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return mockInterviewRepository.findAll().stream()
                .filter(i -> "scheduled".equalsIgnoreCase(i.getStatus()))
                .filter(i -> i.getDate().isAfter(today) || (i.getDate().isEqual(today) && i.getTime().isAfter(now)))
                .collect(Collectors.toList());
    }

    public List<MockInterview> getCompletedInterviews() {
        return mockInterviewRepository.findAll().stream()
                .filter(i -> "completed".equalsIgnoreCase(i.getStatus()))
                .collect(Collectors.toList());
    }
}