package com.example.DeliveryTeamDashboard.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.DeliveryTeamDashboard.Entity.Client;
import com.example.DeliveryTeamDashboard.Entity.ClientInterview;
import com.example.DeliveryTeamDashboard.Entity.Employee;
import com.example.DeliveryTeamDashboard.Entity.JobDescription;
import com.example.DeliveryTeamDashboard.Repository.ClientInterviewRepository;
import com.example.DeliveryTeamDashboard.Repository.ClientRepository;
import com.example.DeliveryTeamDashboard.Repository.EmployeeRepository;
import com.example.DeliveryTeamDashboard.Repository.JobDescriptionRepository;

@Service
public class SalesTeamService {

//    private final EmployeeRepository employeeRepository;
//    private final ClientInterviewRepository clientInterviewRepository;
//    private final ClientRepository clientRepository;
//    private final JobDescriptionRepository jobDescriptionRepository;
//    private final S3Service s3Service;
//
//    public SalesTeamService(EmployeeRepository employeeRepository,
//                            ClientInterviewRepository clientInterviewRepository,
//                            ClientRepository clientRepository,
//                            JobDescriptionRepository jobDescriptionRepository,
//                            S3Service s3Service) {
//        this.employeeRepository = employeeRepository;
//        this.clientInterviewRepository = clientInterviewRepository;
//        this.clientRepository = clientRepository;
//        this.jobDescriptionRepository = jobDescriptionRepository;
//        this.s3Service = s3Service;
//    }
//
//    public List<Employee> getCandidates(String technology, String status, String resourceType) {
//        List<Employee> employees = employeeRepository.findAll();
//        return employees.stream()
//                .filter(e -> "all".equalsIgnoreCase(technology) || e.getTechnology().equalsIgnoreCase(technology))
//                .filter(e -> "all".equalsIgnoreCase(resourceType) || e.getResourceType().equalsIgnoreCase(resourceType))
//                .toList();
//    }
//
//    public ClientInterview scheduleClientInterview(Long employeeId, String client, LocalDate date, Integer level, String jdTitle, String meetingLink) {
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new RuntimeException("Employee not found"));
//        ClientInterview interview = new ClientInterview();
//        interview.setEmployee(employee);
//        interview.setClient(client);
//        interview.setDate(date);
//        interview.setLevel(level);
//        interview.setJobDescriptionTitle(jdTitle);
//        interview.setMeetingLink(meetingLink);
//        interview.setStatus("scheduled");
//        return clientInterviewRepository.save(interview);
//    }
//
//    public ClientInterview updateClientInterview(Long interviewId, String result, String feedback, Integer technicalScore, Integer communicationScore) {
//        ClientInterview interview = clientInterviewRepository.findById(interviewId)
//                .orElseThrow(() -> new RuntimeException("Interview not found"));
//        interview.setResult(result);
//        interview.setFeedback(feedback);
//        interview.setTechnicalScore(technicalScore);
//        interview.setCommunicationScore(communicationScore);
//        interview.setStatus("completed");
//        return clientInterviewRepository.save(interview);
//    }
//
//    public List<ClientInterview> getClientInterviews(String search) {
//        if (search != null && !search.isEmpty()) {
//            return clientInterviewRepository.findByClientContainingIgnoreCase(search);
//        }
//        return clientInterviewRepository.findAll();
//    }
//
//    public Client addClient(String name, String contactEmail, Integer activePositions, List<String> technologies) {
//        Client client = new Client();
//        client.setName(name);
//        client.setContactEmail(contactEmail);
//        client.setActivePositions(activePositions);
//        client.setTechnologies(technologies);
//        return clientRepository.save(client);
//    }
//
//    public List<Client> getClients(String search) {
//        if (search != null && !search.isEmpty()) {
//            return clientRepository.findByNameContainingIgnoreCase(search);
//        }
//        return clientRepository.findAll();
//    }
//
//    public JobDescription uploadJobDescription(String title, String client, LocalDate receivedDate,
//                                              LocalDate deadline, String technology, String resourceType,
//                                              String description, MultipartFile file) throws IOException {
//        String s3Key = file != null ? s3Service.uploadFile(file, "job-descriptions") : null;
//        JobDescription jd = new JobDescription();
//        jd.setTitle(title);
//        jd.setClient(client);
//        jd.setReceivedDate(receivedDate);
//        jd.setDeadline(deadline);
//        jd.setTechnology(technology);
//        jd.setResourceType(resourceType);
//        jd.setDescription(description);
//        jd.setS3Key(s3Key);
//        return jobDescriptionRepository.save(jd);
//    }
//
//    public byte[] downloadJobDescription(Long jdId) throws IOException {
//        JobDescription jd = jobDescriptionRepository.findById(jdId)
//                .orElseThrow(() -> new RuntimeException("Job Description not found"));
//        if (jd.getS3Key() == null) {
//            throw new RuntimeException("No file associated with this Job Description");
//        }
//        return s3Service.downloadFile(jd.getS3Key());
//    }
//
//    public void deleteJobDescription(Long jdId) {
//        JobDescription jd = jobDescriptionRepository.findById(jdId)
//                .orElseThrow(() -> new RuntimeException("Job Description not found"));
//        if (jd.getS3Key() != null) {
//            s3Service.deleteFile(jd.getS3Key());
//        }
//        jobDescriptionRepository.delete(jd);
//    }
	
	private final EmployeeRepository employeeRepository;
    private final ClientInterviewRepository clientInterviewRepository;
    private final ClientRepository clientRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final S3Service s3Service;
    private final EmployeeService employeeService;

    public SalesTeamService(EmployeeRepository employeeRepository,
                            ClientInterviewRepository clientInterviewRepository,
                            ClientRepository clientRepository,
                            JobDescriptionRepository jobDescriptionRepository,
                            S3Service s3Service,
                            EmployeeService employeeService) {
        this.employeeRepository = employeeRepository;
        this.clientInterviewRepository = clientInterviewRepository;
        this.clientRepository = clientRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.s3Service = s3Service;
        this.employeeService = employeeService;
    }

    public List<Employee> getCandidates(String technology, String status, String resourceType) {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .filter(e -> "all".equalsIgnoreCase(technology) || e.getTechnology().equalsIgnoreCase(technology))
                .filter(e -> "all".equalsIgnoreCase(status) || e.getStatus().equalsIgnoreCase(status))
                .filter(e -> "all".equalsIgnoreCase(resourceType) || e.getResourceType().equalsIgnoreCase(resourceType))
                .collect(Collectors.toList());
    }

    public ClientInterview scheduleClientInterview(String empId, String client, LocalDate date, LocalTime time, Integer level, String jobDescriptionTitle, String meetingLink) {
        return (ClientInterview) employeeService.scheduleInterview(empId, "client", date, time, client, null, level, jobDescriptionTitle, meetingLink);
    }

    public ClientInterview updateClientInterview(Long interviewId, String result, String feedback, Integer technicalScore, Integer communicationScore) {
        ClientInterview interview = clientInterviewRepository.findById(interviewId)
                .orElseThrow(() -> new IllegalArgumentException("Interview not found with ID: " + interviewId));
        interview.setResult(result);
        interview.setFeedback(feedback);
        interview.setTechnicalScore(technicalScore);
        interview.setCommunicationScore(communicationScore);
        interview.setStatus("completed");
        return clientInterviewRepository.save(interview);
    }

    public List<ClientInterview> getClientInterviews(String search) {
        if (search != null && !search.isEmpty()) {
            return clientInterviewRepository.findByClientContainingIgnoreCase(search);
        }
        return clientInterviewRepository.findAll();
    }

    public Client addClient(String name, String contactEmail, Integer activePositions, List<String> technologies) {
        Client client = new Client();
        client.setName(name);
        client.setContactEmail(contactEmail);
        client.setActivePositions(activePositions);
        client.setTechnologies(technologies);
        return clientRepository.save(client);
    }

    public List<Client> getClients(String search) {
        if (search != null && !search.isEmpty()) {
            return clientRepository.findByNameContainingIgnoreCase(search);
        }
        return clientRepository.findAll();
    }

    public JobDescription uploadJobDescription(String title, String client, LocalDate receivedDate,
                                              LocalDate deadline, String technology, String resourceType,
                                              String description, MultipartFile file) throws IOException {
        String s3Key = file != null ? s3Service.uploadFile(file, "job-descriptions") : null;
        JobDescription jd = new JobDescription();
        jd.setTitle(title);
        jd.setClient(client);
        jd.setReceivedDate(receivedDate);
        jd.setDeadline(deadline);
        jd.setTechnology(technology);
        jd.setResourceType(resourceType);
        jd.setDescription(description);
        jd.setS3Key(s3Key);
        return jobDescriptionRepository.save(jd);
    }

    public byte[] downloadJobDescription(Long jdId) throws IOException {
        JobDescription jd = jobDescriptionRepository.findById(jdId)
                .orElseThrow(() -> new IllegalArgumentException("Job Description not found with ID: " + jdId));
        if (jd.getS3Key() == null) {
            throw new IllegalArgumentException("No file associated with this Job Description");
        }
        return s3Service.downloadFile(jd.getS3Key());
    }

    public void deleteJobDescription(Long jdId) {
        JobDescription jd = jobDescriptionRepository.findById(jdId)
                .orElseThrow(() -> new IllegalArgumentException("Job Description not found with ID: " + jdId));
        if (jd.getS3Key() != null) {
            s3Service.deleteFile(jd.getS3Key());
        }
        jobDescriptionRepository.delete(jd);
    }

}