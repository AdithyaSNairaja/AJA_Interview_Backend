package com.example.DeliveryTeamDashboard.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.example.DeliveryTeamDashboard.Entity.Client;
import com.example.DeliveryTeamDashboard.Entity.ClientInterview;
import com.example.DeliveryTeamDashboard.Entity.Employee;
import com.example.DeliveryTeamDashboard.Entity.JobDescription;
import com.example.DeliveryTeamDashboard.Service.SalesTeamService;

@RestController
@RequestMapping("/api/sales")
public class SalesTeamController {


	 private final SalesTeamService salesTeamService;

	    public SalesTeamController(SalesTeamService salesTeamService) {
	        this.salesTeamService = salesTeamService;
	    }

	    @GetMapping("/candidates")
	    public List<Employee> getCandidates(
	            @RequestParam(defaultValue = "all") String technology,
	            @RequestParam(defaultValue = "all") String status,
	            @RequestParam(defaultValue = "all") String resourceType) {
	        return salesTeamService.getCandidates(technology, status, resourceType);
	    }


	    @PostMapping("/interviews/schedule")
	    @PreAuthorize("hasRole('SALES_TEAM')")
	    public ResponseEntity<?> scheduleInterview(
	            Authentication authentication,
	            @RequestParam String empId,
	            @RequestParam String interviewType,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
	            @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime time,
	            @RequestParam(required = false) String client,
	            @RequestParam(required = false) String interviewer,
	            @RequestParam(required = false) Integer level,
	            @RequestParam(required = false) String jobDescriptionTitle,
	            @RequestParam(required = false) String meetingLink) {
	        if (authentication == null || !authentication.isAuthenticated()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
	        }
	        if (!"client".equalsIgnoreCase(interviewType)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sales team can only schedule client interviews");
	        }
	        if (client == null || level == null || jobDescriptionTitle == null || meetingLink == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Client, level, job description title, and meeting link are required for client interviews");
	        }
	        try {
	            ClientInterview interview = salesTeamService.scheduleClientInterview(empId, client, date, time, level, jobDescriptionTitle, meetingLink);
	            return ResponseEntity.status(HttpStatus.CREATED).body(interview);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }

	    @PutMapping("/client-interviews/{interviewId}")
	    @PreAuthorize("hasRole('SALES_TEAM')")
	    public ResponseEntity<?> updateClientInterview(
	            Authentication authentication,
	            @PathVariable Long interviewId,
	            @RequestParam String result,
	            @RequestParam String feedback,
	            @RequestParam Integer technicalScore,
	            @RequestParam Integer communicationScore) {
	        if (authentication == null || !authentication.isAuthenticated()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
	        }
	        try {
	            ClientInterview interview = salesTeamService.updateClientInterview(interviewId, result, feedback, technicalScore, communicationScore);
	            return ResponseEntity.ok(interview);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }

	    @GetMapping("/client-interviews")
	    public List<ClientInterview> getClientInterviews(
	            @RequestParam(required = false) String search) {
	        return salesTeamService.getClientInterviews(search);
	    }

	    @PostMapping("/clients")
	    @PreAuthorize("hasRole('SALES_TEAM')")
	    public ResponseEntity<?> addClient(
	            Authentication authentication,
	            @RequestParam String name,
	            @RequestParam String contactEmail,
	            @RequestParam Integer activePositions,
	            @RequestParam List<String> technologies) {
	        if (authentication == null || !authentication.isAuthenticated()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
	        }
	        try {
	            Client client = salesTeamService.addClient(name, contactEmail, activePositions, technologies);
	            return ResponseEntity.status(HttpStatus.CREATED).body(client);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }

	    @GetMapping("/clients")
	    public List<Client> getClients(
	            @RequestParam(required = false) String search) {
	        return salesTeamService.getClients(search);
	    }

	    @PostMapping("/job-descriptions")
	    @PreAuthorize("hasRole('SALES_TEAM')")
	    public ResponseEntity<?> addJobDescription(
	            Authentication authentication,
	            @RequestParam String title,
	            @RequestParam String client,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate receivedDate,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
	            @RequestParam String technology,
	            @RequestParam String resourceType,
	            @RequestParam String description,
	            @RequestParam(required = false) MultipartFile file) throws IOException {
	        if (authentication == null || !authentication.isAuthenticated()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
	        }
	        try {
	            JobDescription jobDescription = salesTeamService.uploadJobDescription(title, client, receivedDate, deadline, technology, resourceType, description, file);
	            return ResponseEntity.status(HttpStatus.CREATED).body(jobDescription);
	        } catch (IllegalArgumentException | IOException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }

	    @GetMapping("/job-descriptions/{jdId}/download")
	    public ResponseEntity<?> downloadJobDescription(@PathVariable Long jdId) {
	        try {
	            byte[] fileData = salesTeamService.downloadJobDescription(jdId);
	            ByteArrayResource resource = new ByteArrayResource(fileData);
	            return ResponseEntity.ok()
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=job_description.pdf")
	                    .contentType(MediaType.APPLICATION_PDF)
	                    .contentLength(fileData.length)
	                    .body(resource);
	        } catch (IllegalArgumentException | IOException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }

	    @DeleteMapping("/job-descriptions/{jdId}")
	    public ResponseEntity<?> deleteJobDescription(@PathVariable Long jdId) {
	        try {
	            salesTeamService.deleteJobDescription(jdId);
	            return ResponseEntity.ok().build();
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }
}