package com.example.DeliveryTeamDashboard.Controller;

import java.io.IOException; 
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.DeliveryTeamDashboard.Entity.Client;
import com.example.DeliveryTeamDashboard.Entity.ClientInterview;
import com.example.DeliveryTeamDashboard.Entity.Employee;
import com.example.DeliveryTeamDashboard.Entity.JobDescription;
import com.example.DeliveryTeamDashboard.Entity.User;
import com.example.DeliveryTeamDashboard.Repository.ClientInterviewRepository;
import com.example.DeliveryTeamDashboard.Service.SalesTeamService;
import com.example.DeliveryTeamDashboard.Service.SalesTeamService.ClientInterviewSchedule;

@RestController
@RequestMapping("/api/sales")
public class SalesTeamController {

		
	@Autowired
	private ClientInterviewRepository clientInterviewRepository;
	
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
	            @RequestParam(required = false) String meetingLink,
	            @RequestParam(required = false, defaultValue = "false") Boolean deployedStatus) {
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
	            ClientInterview interview = salesTeamService.scheduleClientInterview(empId, client, date, time, level, jobDescriptionTitle, meetingLink, deployedStatus);
	            return ResponseEntity.status(HttpStatus.CREATED).body(interview);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }

//	    @PutMapping("/client-interviews/{interviewId}")
//	    @PreAuthorize("hasRole('SALES_TEAM')")
//	    public ResponseEntity<?> updateClientInterview(
//	            Authentication authentication,
//	            @PathVariable Long interviewId,
//	            @RequestBody Map<String, Object> requestBody) {
//	        if (authentication == null || !authentication.isAuthenticated()) {
//	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//	        }
//	        try {
//	            String result = (String) requestBody.get("result");
//	            String feedback = (String) requestBody.get("feedback");
//	            Integer technicalScore = ((Number) requestBody.get("technicalScore")).intValue();
//	            Integer communicationScore = ((Number) requestBody.get("communicationScore")).intValue();
//	            Boolean deployedStatus = (Boolean) requestBody.get("deployedStatus");
//
//	            if (result == null || feedback == null || technicalScore == null || communicationScore == null) {
//	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All required fields (result, feedback, technicalScore, communicationScore) are missing.");
//	            }
//
//	            ClientInterview interview = salesTeamService.updateClientInterview(interviewId, result, feedback, technicalScore, communicationScore, deployedStatus);
//	            return ResponseEntity.ok(interview);
//	        } catch (IllegalArgumentException e) {
//	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//	        } catch (ClassCastException e) {
//	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data types in request: " + e.getMessage());
//	        }
//	    }
	    
	    @PutMapping("/client-interviews/{interviewId}")
	    @PreAuthorize("hasRole('SALES_TEAM')")
	    public ResponseEntity<?> updateClientInterview(
	            Authentication authentication,
	            @PathVariable Long interviewId,
	            @RequestBody Map<String, Object> requestBody) {
	        if (authentication == null || !authentication.isAuthenticated()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
	        }
	        try {
	            String result = (String) requestBody.get("result");
	            String feedback = (String) requestBody.get("feedback");
	            Integer technicalScore = requestBody.get("technicalScore") != null ? ((Number) requestBody.get("technicalScore")).intValue() : null;
	            Integer communicationScore = requestBody.get("communicationScore") != null ? ((Number) requestBody.get("communicationScore")).intValue() : null;
	            Boolean deployedStatus = (Boolean) requestBody.get("deployedStatus");

	            if (result == null || feedback == null || technicalScore == null || communicationScore == null) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All required fields (result, feedback, technicalScore, communicationScore) are missing.");
	            }

	            ClientInterview interview = salesTeamService.updateClientInterview(interviewId, result, feedback, technicalScore, communicationScore, deployedStatus);
	            
	            // Create response with updated interview details
	            Map<String, Object> response = new java.util.HashMap<>();
	            response.put("id", interview.getId());
	            response.put("result", interview.getResult());
	            response.put("feedback", interview.getFeedback());
	            response.put("technicalScore", interview.getTechnicalScore());
	            response.put("communicationScore", interview.getCommunicationScore());
	            response.put("deployedStatus", interview.getDeployedStatus());
	            response.put("level", interview.getLevel());
	            response.put("status", interview.getStatus());

	            return ResponseEntity.ok(response);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        } catch (ClassCastException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data types in request: " + e.getMessage());
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

	    @GetMapping("/job-descriptions")
	    @PreAuthorize("hasRole('SALES_TEAM')")
	    public List<JobDescription> getAllJobDescriptions() {
	        return salesTeamService.getAllJobDescriptions();
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

	    @GetMapping("/resumes")
	    @PreAuthorize("hasRole('SALES_TEAM') or hasRole('DELIVERY_TEAM')")
	    public ResponseEntity<List<Employee>> getAllEmployeeResumes() {
	        List<Employee> employeesWithResumes = salesTeamService.getCandidates("all", "all", "all"); // Assuming getCandidates can fetch employees with resume info
	        return ResponseEntity.ok(employeesWithResumes);
	    }

	    @GetMapping("/resumes/filter")
	    @PreAuthorize("hasRole('SALES_TEAM') or hasRole('DELIVERY_TEAM')")
	    public ResponseEntity<List<Employee>> getFilteredResumes(
	            @RequestParam(required = false, defaultValue = "all") String technology,
	            @RequestParam(required = false, defaultValue = "all") String resourceType) {
	        try {
	            List<Employee> filteredResumes = salesTeamService.getCandidates(technology, "all", resourceType)
	                .stream()
	                .filter(employee -> Boolean.TRUE.equals(employee.getReadyForDeployment()))
	                .toList();
	            return ResponseEntity.ok(filteredResumes);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	        }
	    }

//	    @GetMapping("/client-interviews/{interviewId}/feedback")
//	    @PreAuthorize("hasRole('SALES_TEAM') or hasRole('DELIVERY_TEAM') or hasRole('EMPLOYEE')")
//	    public ResponseEntity<?> getClientInterviewFeedback(@PathVariable Long interviewId) {
//	        try {
//	            ClientInterview interview = salesTeamService.getClientInterviewById(interviewId);
//	            if (interview == null) {
//	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interview not found with ID: " + interviewId);
//	            }
//	            // Return only the feedback-related fields
//	            Map<String, Object> feedbackDetails = new java.util.HashMap<>();
//	            feedbackDetails.put("id", interview.getId());
//	            feedbackDetails.put("feedback", interview.getFeedback());
//	            feedbackDetails.put("technicalScore", interview.getTechnicalScore());
//	            feedbackDetails.put("communicationScore", interview.getCommunicationScore());
//	            feedbackDetails.put("result", interview.getResult());
//	            feedbackDetails.put("overallStatus", interview.getStatus());
//	            return ResponseEntity.ok(feedbackDetails);
//	        } catch (IllegalArgumentException e) {
//	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//	        }
//	    }
	    
	    @GetMapping("/client-interviews/{interviewId}/feedback")
	    @PreAuthorize("hasRole('SALES_TEAM') or hasRole('DELIVERY_TEAM') or hasRole('EMPLOYEE')")
	    public ResponseEntity<?> getClientInterviewFeedback(@PathVariable Long interviewId) {
	        try {
	            ClientInterview interview = salesTeamService.getClientInterviewById(interviewId);
	            if (interview == null) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interview not found with ID: " + interviewId);
	            }
	            Map<String, Object> feedbackDetails = new java.util.HashMap<>();
	            feedbackDetails.put("id", interview.getId());
	            feedbackDetails.put("feedback", interview.getFeedback());
	            feedbackDetails.put("technicalScore", interview.getTechnicalScore());
	            feedbackDetails.put("communicationScore", interview.getCommunicationScore());
	            feedbackDetails.put("result", interview.getResult());
	            feedbackDetails.put("overallStatus", interview.getStatus());
	            feedbackDetails.put("level", interview.getLevel());
	            return ResponseEntity.ok(feedbackDetails);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }


	    @GetMapping("/employees/deployed")
	    @PreAuthorize("hasAnyRole('SALES_TEAM', 'DELIVERY_TEAM', 'EMPLOYEE', 'ADMIN')") // Accessible by all roles
	    public ResponseEntity<List<Employee>> getDeployedEmployees() {
	        List<Employee> deployedEmployees = salesTeamService.getDeployedEmployees();
	        return ResponseEntity.ok(deployedEmployees);
	    }
	    
	    @PostMapping("/employees/{empId}/interviews")
	    @PreAuthorize("isAuthenticated()")
	    public ResponseEntity<?> scheduleMultipleClientInterviews(
	            @PathVariable String empId,
	            @RequestBody List<ClientInterviewSchedule> schedules) {
	        try {
	            List<ClientInterview> interviews = salesTeamService.scheduleMultipleClientInterviews(empId, schedules);
	            return ResponseEntity.ok(interviews);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
	        } catch (AuthenticationException e) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: Invalid or missing token");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
	        }
	    }
	    
	    @GetMapping("/get-all-scheduleclientinterview-count")
	    public int getallscheduleclientinterviewcount() {
	    	List<ClientInterview> in=clientInterviewRepository.findAll();
	    	
	    	return in.size();
	    }
	    
	    @PutMapping("/profile-picture")
	     public ResponseEntity<?> updateProfilePicture(
	             Authentication authentication,
	             @RequestParam Long Id,
	             @RequestParam MultipartFile file) throws IOException {
	         if (authentication == null || !authentication.isAuthenticated()) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
	         }
	         try {
	             User user = salesTeamService.updateProfilePicture(Id, file);
	             return ResponseEntity.status(HttpStatus.OK).body(user);
	         } catch (IllegalArgumentException | IOException e) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	         }
	     }

	     @GetMapping("/profile-picture/{employeeId}")
	     public ResponseEntity<?> getProfilePicture(@PathVariable Long employeeId) throws IOException {
	         try {
	             byte[] fileData = salesTeamService.getProfilePicture(employeeId);
	             ByteArrayResource resource = new ByteArrayResource(fileData);
	             return ResponseEntity.ok()
	                     .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=profile-picture.jpg")
	                     .contentType(MediaType.IMAGE_JPEG)
	                     .contentLength(fileData.length)
	                     .body(resource);
	         } catch (IllegalArgumentException | IOException e) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	         }
	     }

}