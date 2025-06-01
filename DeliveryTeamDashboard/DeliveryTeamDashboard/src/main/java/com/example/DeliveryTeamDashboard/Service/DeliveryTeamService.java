package com.example.DeliveryTeamDashboard.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.DeliveryTeamDashboard.Entity.Employee;
import com.example.DeliveryTeamDashboard.Entity.MockInterview;
import com.example.DeliveryTeamDashboard.Repository.EmployeeRepository;
import com.example.DeliveryTeamDashboard.Repository.MockInterviewRepository;

@Service
public class DeliveryTeamService {

//	private final EmployeeRepository employeeRepository;
//    private final MockInterviewRepository mockInterviewRepository;
//
//    public DeliveryTeamService(EmployeeRepository employeeRepository, MockInterviewRepository mockInterviewRepository) {
//        this.employeeRepository = employeeRepository;
//        this.mockInterviewRepository = mockInterviewRepository;
//    }
//
//    public List<Employee> getEmployees(String technology, String resourceType) {
//        List<Employee> employees = employeeRepository.findAll();
//        return employees.stream()
//                .filter(e -> "all".equalsIgnoreCase(technology) || e.getTechnology().equalsIgnoreCase(technology))
//                .filter(e -> "all".equalsIgnoreCase(resourceType) || e.getResourceType().equalsIgnoreCase(resourceType))
//                .collect(Collectors.toList());
//    }
//
//    public MockInterview scheduleMockInterview(String empId, LocalDate date, LocalTime time, String interviewer) {
//        return mockInterviewRepository.save(new EmployeeService(employeeRepository, null, null, mockInterviewRepository, null, null, null)
//                .scheduleMockInterview(empId, date, time, interviewer));
//    }
//
//    public MockInterview updateMockInterviewFeedback(Long interviewId, String feedback, Integer technicalScore, Integer communicationScore) {
//        MockInterview interview = mockInterviewRepository.findById(interviewId)
//                .orElseThrow(() -> new IllegalArgumentException("Interview not found with ID: " + interviewId));
//        interview.setTechnicalFeedback(feedback);
//        interview.setTechnicalRating(technicalScore);
//        interview.setCommunicationRating(communicationScore);
//        interview.setStatus("completed");
//        return mockInterviewRepository.save(interview);
//    }
//
//    public List<MockInterview> getUpcomingInterviews() {
//        LocalDate today = LocalDate.now();
//        LocalTime now = LocalTime.now();
//        return mockInterviewRepository.findAll().stream()
//                .filter(i -> "scheduled".equalsIgnoreCase(i.getStatus()))
//                .filter(i -> i.getDate().isAfter(today) || (i.getDate().isEqual(today) && i.getTime().isAfter(now)))
//                .collect(Collectors.toList());
//    }
//
//    public List<MockInterview> getCompletedInterviews() {
//        return mockInterviewRepository.findAll().stream()
//                .filter(i -> "completed".equalsIgnoreCase(i.getStatus()))
//                .collect(Collectors.toList());
//    }
	
	 private final EmployeeRepository employeeRepository;
	    private final MockInterviewRepository mockInterviewRepository;
	    private final EmployeeService employeeService;

	    public DeliveryTeamService(EmployeeRepository employeeRepository, MockInterviewRepository mockInterviewRepository, EmployeeService employeeService) {
	        this.employeeRepository = employeeRepository;
	        this.mockInterviewRepository = mockInterviewRepository;
	        this.employeeService = employeeService;
	    }

	    public List<Employee> getEmployees(String technology, String resourceType) {
	        List<Employee> employees = employeeRepository.findAll();
	        return employees.stream()
	                .filter(e -> "all".equalsIgnoreCase(technology) || e.getTechnology().equalsIgnoreCase(technology))
	                .filter(e -> "all".equalsIgnoreCase(resourceType) || e.getResourceType().equalsIgnoreCase(resourceType))
	                .collect(Collectors.toList());
	    }

	    public MockInterview scheduleMockInterview(String empId, LocalDate date, LocalTime time, String interviewer) {
	        return (MockInterview) employeeService.scheduleInterview(empId, "mock", date, time, null, interviewer, null, null, null);
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