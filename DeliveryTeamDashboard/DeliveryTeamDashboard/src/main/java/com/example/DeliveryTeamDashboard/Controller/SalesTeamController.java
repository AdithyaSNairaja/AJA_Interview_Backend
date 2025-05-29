package com.example.DeliveryTeamDashboard.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/client-interviews")
    public ClientInterview scheduleClientInterview(
            @RequestParam Long employeeId,
            @RequestParam String client,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer level,
            @RequestParam String jdTitle,
            @RequestParam String meetingLink) {
        return salesTeamService.scheduleClientInterview(employeeId, client, date, level, jdTitle, meetingLink);
    }

    @PutMapping("/client-interviews/{interviewId}")
    public ClientInterview updateClientInterview(
            @PathVariable Long interviewId,
            @RequestParam String result,
            @RequestParam String feedback,
            @RequestParam Integer technicalScore,
            @RequestParam Integer communicationScore) {
        return salesTeamService.updateClientInterview(interviewId, result, feedback, technicalScore, communicationScore);
    }

    @GetMapping("/client-interviews")
    public List<ClientInterview> getClientInterviews(
            @RequestParam(required = false) String search) {
        return salesTeamService.getClientInterviews(search);
    }

    @PostMapping("/clients")
    public Client addClient(
            @RequestParam String name,
            @RequestParam String contactEmail,
            @RequestParam Integer activePositions,
            @RequestParam List<String> technologies) {
        return salesTeamService.addClient(name, contactEmail, activePositions, technologies);
    }

    @GetMapping("/clients")
    public List<Client> getClients(
            @RequestParam(required = false) String search) {
        return salesTeamService.getClients(search);
    }

    @PostMapping("/job-descriptions")
    public JobDescription uploadJobDescription(
            @RequestParam String title,
            @RequestParam String client,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate receivedDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
            @RequestParam String technology,
            @RequestParam String resourceType,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile file) throws IOException {
        return salesTeamService.uploadJobDescription(title, client, receivedDate, deadline,
                technology, resourceType, description, file);
    }

    @GetMapping("/job-descriptions/{jdId}/download")
    public ResponseEntity<Resource> downloadJobDescription(@PathVariable Long jdId) throws IOException {
        byte[] fileData = salesTeamService.downloadJobDescription(jdId);
        ByteArrayResource resource = new ByteArrayResource(fileData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=job_description.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(fileData.length)
                .body(resource);
    }

    @DeleteMapping("/job-descriptions/{jdId}")
    public void deleteJobDescription(@PathVariable Long jdId) {
        salesTeamService.deleteJobDescription(jdId);
    }
}