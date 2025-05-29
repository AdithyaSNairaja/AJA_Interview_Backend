package com.example.DeliveryTeamDashboard.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.DeliveryTeamDashboard.Entity.ClientInterview;
import com.example.DeliveryTeamDashboard.Entity.Employee;
import com.example.DeliveryTeamDashboard.Entity.InterviewQuestion;
import com.example.DeliveryTeamDashboard.Entity.JobDescription;
import com.example.DeliveryTeamDashboard.Entity.MockInterview;
import com.example.DeliveryTeamDashboard.Entity.Resume;
import com.example.DeliveryTeamDashboard.Repository.EmployeeRepository;
import com.example.DeliveryTeamDashboard.Service.EmployeeService;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

	 private final EmployeeService employeeService;
     private final EmployeeRepository employeeRepository;

     public EmployeeController(EmployeeService employeeService, EmployeeRepository employeeRepository) {
         this.employeeService = employeeService;
         this.employeeRepository = employeeRepository;
     }

     @GetMapping("/me")
     public ResponseEntity<Employee> getEmployeeDetails(Authentication authentication) {
         if (authentication == null || !authentication.isAuthenticated()) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
         }
         String email = authentication.getName();
         return employeeRepository.findByUserEmail(email)
                 .map(ResponseEntity::ok)
                 .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
     }

     @PutMapping("/me")
     public ResponseEntity<Employee> updateEmployeeDetails(
             Authentication authentication,
             @RequestParam(required = false) String technology,
             @RequestParam(required = false) String empId) {
         if (authentication == null || !authentication.isAuthenticated()) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
         }
         String email = authentication.getName();
         return employeeRepository.findByUserEmail(email)
                 .map(employee -> {
                     Employee updatedEmployee = employeeService.updateEmployeeDetails(
                             employee.getId(), technology, empId);
                     return ResponseEntity.ok(updatedEmployee);
                 })
                 .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
     }

     @GetMapping("/job-descriptions")
     public ResponseEntity<List<JobDescription>> getJobDescriptions(
             @RequestParam(required = false) String search,
             @RequestParam(defaultValue = "all") String technology,
             @RequestParam(defaultValue = "all") String resourceType) {
         List<JobDescription> jobDescriptions = employeeService.getJobDescriptions(search, technology, resourceType);
         return ResponseEntity.ok(jobDescriptions);
     }

     @PostMapping("/resumes")
     public ResponseEntity<Resume> uploadResume(
             @RequestParam("employeeId") Long employeeId,
             @RequestParam("jdId") Long jdId,
             @RequestParam("file") MultipartFile file) throws IOException {
         Resume resume = employeeService.uploadResume(employeeId, jdId, file);
         return ResponseEntity.status(HttpStatus.CREATED).body(resume);
     }

     @GetMapping("/resumes/{resumeId}/download")
     public ResponseEntity<Resource> downloadResume(@PathVariable Long resumeId) throws IOException {
         byte[] fileData = employeeService.downloadResume(resumeId);
         ByteArrayResource resource = new ByteArrayResource(fileData);
         return ResponseEntity.ok()
                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resume.pdf")
                 .contentType(MediaType.APPLICATION_PDF)
                 .contentLength(fileData.length)
                 .body(resource);
     }

     @DeleteMapping("/resumes/{resumeId}")
     public ResponseEntity<Void> deleteResume(@PathVariable Long resumeId) {
         employeeService.deleteResume(resumeId);
         return ResponseEntity.noContent().build();
     }

     @GetMapping("/mock-interviews")
     public ResponseEntity<List<MockInterview>> getMockInterviews(
             @RequestParam(required = false) Long employeeId,
             @RequestParam(defaultValue = "all") String technology,
             @RequestParam(defaultValue = "all") String resourceType) {
         List<MockInterview> interviews = employeeService.getMockInterviews(employeeId, technology, resourceType);
         return ResponseEntity.ok(interviews);
     }

     @GetMapping("/client-interviews")
     public ResponseEntity<List<ClientInterview>> getClientInterviews(
             @RequestParam(required = false) Long employeeId,
             @RequestParam(defaultValue = "all") String technology,
             @RequestParam(defaultValue = "all") String resourceType) {
         List<ClientInterview> interviews = employeeService.getClientInterviews(employeeId, technology, resourceType);
         return ResponseEntity.ok(interviews);
     }

     @PostMapping("/interview-questions")
     public ResponseEntity<InterviewQuestion> addInterviewQuestion(
             @RequestParam String technology,
             @RequestParam String question,
             @RequestParam String user) {
         InterviewQuestion interviewQuestion = employeeService.addInterviewQuestion(technology, question, user);
         return ResponseEntity.status(HttpStatus.CREATED).body(interviewQuestion);
     }

     @GetMapping("/interview-questions")
     public ResponseEntity<List<InterviewQuestion>> getInterviewQuestions(
             @RequestParam(defaultValue = "all") String technology) {
         List<InterviewQuestion> questions = employeeService.getInterviewQuestions(technology);
         return ResponseEntity.ok(questions);
     }  
    }
